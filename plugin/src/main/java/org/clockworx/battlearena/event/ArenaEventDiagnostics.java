package org.clockworx.battlearena.event;

import org.clockworx.battlearena.Arena;
import org.clockworx.battlearena.ArenaPlayer;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.competition.Competition;
import org.clockworx.battlearena.event.action.EventAction;
import org.clockworx.battlearena.util.LogCategory;
import org.clockworx.battlearena.util.PluginLogger;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Diagnostics system for tracking arena event execution, errors, and performance.
 * Provides metrics, error tracking, and optional trace logging for debugging.
 * 
 * @author Clockworx
 * @since 5.0.3
 */
public class ArenaEventDiagnostics {
    
    /**
     * Represents a single error occurrence with context.
     */
    public static class ErrorRecord {
        private final Instant timestamp;
        private final String eventType;
        private final String arenaName;
        private final String actionClass;
        private final String phase;
        private final String errorMessage;
        private final String stackTrace;
        
        public ErrorRecord(Instant timestamp, String eventType, String arenaName, 
                          String actionClass, String phase, String errorMessage, String stackTrace) {
            this.timestamp = timestamp;
            this.eventType = eventType;
            this.arenaName = arenaName;
            this.actionClass = actionClass;
            this.phase = phase;
            this.errorMessage = errorMessage;
            this.stackTrace = stackTrace;
        }
        
        public Instant getTimestamp() { return timestamp; }
        public String getEventType() { return eventType; }
        public String getArenaName() { return arenaName; }
        public String getActionClass() { return actionClass; }
        public String getPhase() { return phase; }
        public String getErrorMessage() { return errorMessage; }
        public String getStackTrace() { return stackTrace; }
    }
    
    /**
     * Metrics for a single event type.
     */
    public static class EventMetrics {
        private final AtomicLong triggered = new AtomicLong(0);
        private final AtomicLong actionsExecuted = new AtomicLong(0);
        private final AtomicLong preProcessFailures = new AtomicLong(0);
        private final AtomicLong processFailures = new AtomicLong(0);
        private final AtomicLong postProcessFailures = new AtomicLong(0);
        
        public void incrementTriggered() { triggered.incrementAndGet(); }
        public void incrementActionsExecuted() { actionsExecuted.incrementAndGet(); }
        public void incrementPreProcessFailures() { preProcessFailures.incrementAndGet(); }
        public void incrementProcessFailures() { processFailures.incrementAndGet(); }
        public void incrementPostProcessFailures() { postProcessFailures.incrementAndGet(); }
        
        public long getTriggered() { return triggered.get(); }
        public long getActionsExecuted() { return actionsExecuted.get(); }
        public long getPreProcessFailures() { return preProcessFailures.get(); }
        public long getProcessFailures() { return processFailures.get(); }
        public long getPostProcessFailures() { return postProcessFailures.get(); }
        public long getTotalFailures() {
            return preProcessFailures.get() + processFailures.get() + postProcessFailures.get();
        }
    }
    
    private final BattleArena plugin;
    private final PluginLogger logger;
    
    // Per-event-type metrics
    private final Map<String, EventMetrics> eventMetrics = new ConcurrentHashMap<>();
    
    // Last error per event type (configurable retention)
    private final Map<String, ErrorRecord> lastErrors = new ConcurrentHashMap<>();
    private final int maxErrorRecords;
    
    // Trace logging configuration
    private final Map<String, Boolean> traceEnabledForArena = new ConcurrentHashMap<>();
    private final Map<String, Boolean> traceEnabledForEventType = new ConcurrentHashMap<>();
    private boolean globalTraceEnabled = false;
    
    /**
     * Creates a new diagnostics instance.
     * 
     * @param plugin The plugin instance
     * @param logger The logger to use
     * @param maxErrorRecords Maximum number of error records to retain (0 = unlimited)
     */
    public ArenaEventDiagnostics(BattleArena plugin, PluginLogger logger, int maxErrorRecords) {
        this.plugin = plugin;
        this.logger = logger;
        this.maxErrorRecords = maxErrorRecords;
    }
    
    /**
     * Records that an event was triggered.
     * 
     * @param eventType The event type name
     * @param arena The arena
     */
    public void recordEventTriggered(String eventType, Arena arena) {
        EventMetrics metrics = eventMetrics.computeIfAbsent(eventType, k -> new EventMetrics());
        metrics.incrementTriggered();
        
        if (shouldTrace(arena.getName(), eventType) && logger != null) {
            logger.debug(LogCategory.EVENT, String.format("Event triggered: %s in arena %s", eventType, arena.getName()));
        }
    }
    
    /**
     * Records that an action was executed successfully.
     * 
     * @param eventType The event type name
     * @param action The action that was executed
     */
    public void recordActionExecuted(String eventType, EventAction action) {
        EventMetrics metrics = eventMetrics.computeIfAbsent(eventType, k -> new EventMetrics());
        metrics.incrementActionsExecuted();
    }
    
    /**
     * Records a failure during pre-processing.
     * 
     * @param eventType The event type name
     * @param arena The arena
     * @param action The action that failed
     * @param error The error that occurred
     */
    public void recordPreProcessFailure(String eventType, Arena arena, EventAction action, Throwable error) {
        EventMetrics metrics = eventMetrics.computeIfAbsent(eventType, k -> new EventMetrics());
        metrics.incrementPreProcessFailures();
        
        recordError(eventType, arena, action, "pre-process", error);
    }
    
    /**
     * Records a failure during action processing.
     * 
     * @param eventType The event type name
     * @param arena The arena
     * @param action The action that failed
     * @param error The error that occurred
     */
    public void recordProcessFailure(String eventType, Arena arena, EventAction action, Throwable error) {
        EventMetrics metrics = eventMetrics.computeIfAbsent(eventType, k -> new EventMetrics());
        metrics.incrementProcessFailures();
        
        recordError(eventType, arena, action, "process", error);
    }
    
    /**
     * Records a failure during post-processing.
     * 
     * @param eventType The event type name
     * @param arena The arena
     * @param action The action that failed
     * @param error The error that occurred
     */
    public void recordPostProcessFailure(String eventType, Arena arena, EventAction action, Throwable error) {
        EventMetrics metrics = eventMetrics.computeIfAbsent(eventType, k -> new EventMetrics());
        metrics.incrementPostProcessFailures();
        
        recordError(eventType, arena, action, "post-process", error);
    }
    
    /**
     * Records an error with full context.
     */
    private void recordError(String eventType, Arena arena, EventAction action, String phase, Throwable error) {
        String actionClass = action != null ? action.getClass().getSimpleName() : "unknown";
        String errorMessage = error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
        
        // Generate stack trace
        java.io.StringWriter sw = new java.io.StringWriter();
        error.printStackTrace(new java.io.PrintWriter(sw));
        String stackTrace = sw.toString();
        
        ErrorRecord record = new ErrorRecord(
            Instant.now(),
            eventType,
            arena.getName(),
            actionClass,
            phase,
            errorMessage,
            stackTrace
        );
        
        lastErrors.put(eventType, record);
        
        // Log the error
        String errorMsg = String.format("Event action failure in %s phase for event %s in arena %s: %s - %s",
            phase, eventType, arena.getName(), actionClass, errorMessage);
        if (logger != null) {
            logger.warning(LogCategory.EVENT, errorMsg, error);
        } else {
            // Fallback to plugin logger if PluginLogger is not available
            arena.getPlugin().warn(errorMsg, error);
        }
        
        // Cleanup old records if limit is set
        if (maxErrorRecords > 0 && lastErrors.size() > maxErrorRecords) {
            // Remove oldest entry (simple FIFO - could be improved with timestamp tracking)
            String oldestKey = lastErrors.keySet().iterator().next();
            lastErrors.remove(oldestKey);
        }
    }
    
    /**
     * Gets metrics for a specific event type.
     * 
     * @param eventType The event type name
     * @return The metrics, or null if no events of this type have been recorded
     */
    @Nullable
    public EventMetrics getMetrics(String eventType) {
        return eventMetrics.get(eventType);
    }
    
    /**
     * Gets all event metrics.
     * 
     * @return A copy of all metrics
     */
    public Map<String, EventMetrics> getAllMetrics() {
        return new HashMap<>(eventMetrics);
    }
    
    /**
     * Gets the last error for a specific event type.
     * 
     * @param eventType The event type name
     * @return The last error record, or null if none
     */
    @Nullable
    public ErrorRecord getLastError(String eventType) {
        return lastErrors.get(eventType);
    }
    
    /**
     * Gets all last error records.
     * 
     * @return A copy of all error records
     */
    public Map<String, ErrorRecord> getAllLastErrors() {
        return new HashMap<>(lastErrors);
    }
    
    /**
     * Enables trace logging for a specific arena.
     * 
     * @param arenaName The arena name
     * @param enabled Whether to enable tracing
     */
    public void setTraceEnabledForArena(String arenaName, boolean enabled) {
        traceEnabledForArena.put(arenaName, enabled);
    }
    
    /**
     * Enables trace logging for a specific event type.
     * 
     * @param eventType The event type name
     * @param enabled Whether to enable tracing
     */
    public void setTraceEnabledForEventType(String eventType, boolean enabled) {
        traceEnabledForEventType.put(eventType, enabled);
    }
    
    /**
     * Sets global trace logging.
     * 
     * @param enabled Whether to enable global tracing
     */
    public void setGlobalTraceEnabled(boolean enabled) {
        this.globalTraceEnabled = enabled;
    }
    
    /**
     * Checks if trace logging should be enabled for a given arena and event type.
     */
    private boolean shouldTrace(String arenaName, String eventType) {
        if (globalTraceEnabled) {
            return true;
        }
        
        Boolean arenaEnabled = traceEnabledForArena.get(arenaName);
        if (arenaEnabled != null && arenaEnabled) {
            return true;
        }
        
        Boolean eventEnabled = traceEnabledForEventType.get(eventType);
        return eventEnabled != null && eventEnabled;
    }
    
    /**
     * Resets all metrics and error records.
     */
    public void reset() {
        eventMetrics.clear();
        lastErrors.clear();
        if (logger != null) {
            logger.info(LogCategory.EVENT, "Event diagnostics reset");
        } else {
            plugin.info("Event diagnostics reset");
        }
    }
    
    /**
     * Generates a diagnostic report as a string.
     * 
     * @return The diagnostic report
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== BattleArena Event Diagnostics Report ===\n");
        report.append("Generated: ").append(Instant.now()).append("\n\n");
        
        report.append("--- Event Metrics ---\n");
        if (eventMetrics.isEmpty()) {
            report.append("No events recorded yet.\n");
        } else {
            for (Map.Entry<String, EventMetrics> entry : eventMetrics.entrySet()) {
                String eventType = entry.getKey();
                EventMetrics metrics = entry.getValue();
                report.append(String.format("%s:\n", eventType));
                report.append(String.format("  Triggered: %d\n", metrics.getTriggered()));
                report.append(String.format("  Actions Executed: %d\n", metrics.getActionsExecuted()));
                report.append(String.format("  Pre-Process Failures: %d\n", metrics.getPreProcessFailures()));
                report.append(String.format("  Process Failures: %d\n", metrics.getProcessFailures()));
                report.append(String.format("  Post-Process Failures: %d\n", metrics.getPostProcessFailures()));
                report.append(String.format("  Total Failures: %d\n", metrics.getTotalFailures()));
                report.append("\n");
            }
        }
        
        report.append("--- Last Errors ---\n");
        if (lastErrors.isEmpty()) {
            report.append("No errors recorded.\n");
        } else {
            for (Map.Entry<String, ErrorRecord> entry : lastErrors.entrySet()) {
                String eventType = entry.getKey();
                ErrorRecord error = entry.getValue();
                report.append(String.format("%s:\n", eventType));
                report.append(String.format("  Timestamp: %s\n", error.getTimestamp()));
                report.append(String.format("  Arena: %s\n", error.getArenaName()));
                report.append(String.format("  Action: %s\n", error.getActionClass()));
                report.append(String.format("  Phase: %s\n", error.getPhase()));
                report.append(String.format("  Error: %s\n", error.getErrorMessage()));
                report.append("\n");
            }
        }
        
        return report.toString();
    }
}
