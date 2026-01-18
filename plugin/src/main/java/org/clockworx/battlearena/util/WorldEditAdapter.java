package org.clockworx.battlearena.util;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.clockworx.battlearena.BattleArena;
import org.clockworx.battlearena.competition.map.options.Bounds;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;

/**
 * Reflection-based adapter for WorldEdit/FAWE operations.
 * This avoids hard-linking to WorldEdit classes, preventing classloading errors
 * when WorldEdit/FAWE is not installed.
 * 
 * @author Clockworx
 * @since 5.0.3
 */
public class WorldEditAdapter {
    
    private static final String WORLD_EDIT_CLASS = "com.sk89q.worldedit.WorldEdit";
    private static final String WORLD_EDIT_EXCEPTION_CLASS = "com.sk89q.worldedit.WorldEditException";
    private static final String BUKKIT_ADAPTER_CLASS = "com.sk89q.worldedit.bukkit.BukkitAdapter";
    private static final String EDIT_SESSION_CLASS = "com.sk89q.worldedit.EditSession";
    private static final String BLOCK_ARRAY_CLIPBOARD_CLASS = "com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard";
    private static final String FORWARD_EXTENT_COPY_CLASS = "com.sk89q.worldedit.function.operation.ForwardExtentCopy";
    private static final String OPERATIONS_CLASS = "com.sk89q.worldedit.function.operation.Operations";
    private static final String BLOCK_VECTOR3_CLASS = "com.sk89q.worldedit.math.BlockVector3";
    private static final String CUBOID_REGION_CLASS = "com.sk89q.worldedit.regions.CuboidRegion";
    private static final String CLIPBOARD_HOLDER_CLASS = "com.sk89q.worldedit.session.ClipboardHolder";
    private static final String CLIPBOARD_CLASS = "com.sk89q.worldedit.extent.clipboard.Clipboard";
    private static final String CLIPBOARD_FORMAT_CLASS = "com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat";
    private static final String CLIPBOARD_FORMATS_CLASS = "com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats";
    private static final String CLIPBOARD_READER_CLASS = "com.sk89q.worldedit.extent.clipboard.io.ClipboardReader";
    private static final String CLIPBOARD_WRITER_CLASS = "com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter";
    private static final String BUILT_IN_CLIPBOARD_FORMAT_CLASS = "com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat";
    
    private final PluginManager pluginManager;
    private final Plugin worldEditPlugin;
    private final ClassLoader worldEditClassLoader;
    private boolean available = false;
    
    // Cached classes
    private Class<?> worldEditClass;
    private Class<?> worldEditExceptionClass;
    private Class<?> bukkitAdapterClass;
    private Class<?> editSessionClass;
    private Class<?> blockArrayClipboardClass;
    private Class<?> forwardExtentCopyClass;
    private Class<?> operationsClass;
    private Class<?> blockVector3Class;
    private Class<?> cuboidRegionClass;
    private Class<?> clipboardHolderClass;
    private Class<?> clipboardClass;
    private Class<?> clipboardFormatClass;
    private Class<?> clipboardFormatsClass;
    private Class<?> clipboardReaderClass;
    private Class<?> clipboardWriterClass;
    private Class<?> builtInClipboardFormatClass;
    
    // Cached methods
    private Method worldEditGetInstance;
    private Method bukkitAdapterAdapt;
    private Method editSessionNewEditSession;
    private Method blockVector3At;
    private java.lang.reflect.Constructor<?> cuboidRegionConstructor;
    private java.lang.reflect.Constructor<?> blockArrayClipboardConstructor;
    private java.lang.reflect.Constructor<?> forwardExtentCopyConstructor;
    private int forwardExtentCopyParamCount; // Track how many parameters the constructor takes
    private Method operationsComplete;
    private Method clipboardHolderCreatePaste;
    private Method pasteTo;
    private Method pasteIgnoreAirBlocks;
    private Method pasteBuild;
    private Method clipboardFormatsFindByFile;
    private Method clipboardFormatGetReader;
    private Method clipboardReaderRead;
    private Method builtInClipboardFormatGetPrimaryFileExtension;
    private Method builtInClipboardFormatGetWriter;
    private Method clipboardWriterWrite;
    
    /**
     * Creates a new WorldEdit adapter.
     * 
     * @param pluginManager The plugin manager
     * @return The adapter, or null if WorldEdit/FAWE is not available
     */
    @Nullable
    public static WorldEditAdapter create(PluginManager pluginManager) {
        Plugin wePlugin = WorldEditSupport.resolveWorldEditPlugin(pluginManager);
        if (wePlugin == null || !WorldEditSupport.isWorldEditAvailable(pluginManager)) {
            return null;
        }
        
        WorldEditAdapter adapter = new WorldEditAdapter(pluginManager, wePlugin);
        if (adapter.initialize()) {
            return adapter;
        }
        
        return null;
    }
    
    private WorldEditAdapter(PluginManager pluginManager, Plugin worldEditPlugin) {
        this.pluginManager = pluginManager;
        this.worldEditPlugin = worldEditPlugin;
        this.worldEditClassLoader = worldEditPlugin.getClass().getClassLoader();
    }
    
    /**
     * Initializes the adapter by loading and caching WorldEdit classes and methods.
     * 
     * @return true if initialization succeeded
     */
    private boolean initialize() {
        try {
            // Load classes
            worldEditClass = loadClass(WORLD_EDIT_CLASS);
            worldEditExceptionClass = loadClass(WORLD_EDIT_EXCEPTION_CLASS);
            bukkitAdapterClass = loadClass(BUKKIT_ADAPTER_CLASS);
            editSessionClass = loadClass(EDIT_SESSION_CLASS);
            blockArrayClipboardClass = loadClass(BLOCK_ARRAY_CLIPBOARD_CLASS);
            forwardExtentCopyClass = loadClass(FORWARD_EXTENT_COPY_CLASS);
            operationsClass = loadClass(OPERATIONS_CLASS);
            blockVector3Class = loadClass(BLOCK_VECTOR3_CLASS);
            cuboidRegionClass = loadClass(CUBOID_REGION_CLASS);
            clipboardHolderClass = loadClass(CLIPBOARD_HOLDER_CLASS);
            clipboardClass = loadClass(CLIPBOARD_CLASS);
            clipboardFormatClass = loadClass(CLIPBOARD_FORMAT_CLASS);
            clipboardFormatsClass = loadClass(CLIPBOARD_FORMATS_CLASS);
            clipboardReaderClass = loadClass(CLIPBOARD_READER_CLASS);
            clipboardWriterClass = loadClass(CLIPBOARD_WRITER_CLASS);
            builtInClipboardFormatClass = loadClass(BUILT_IN_CLIPBOARD_FORMAT_CLASS);
            
            // Load methods
            worldEditGetInstance = worldEditClass.getMethod("getInstance");
            bukkitAdapterAdapt = bukkitAdapterClass.getMethod("adapt", World.class);
            editSessionNewEditSession = worldEditClass.getMethod("newEditSession", 
                loadClass("com.sk89q.worldedit.world.World"));
            blockVector3At = blockVector3Class.getMethod("at", int.class, int.class, int.class);
            cuboidRegionConstructor = cuboidRegionClass.getConstructor(
                blockVector3Class, blockVector3Class);
            blockArrayClipboardConstructor = blockArrayClipboardClass.getConstructor(
                loadClass("com.sk89q.worldedit.regions.Region"));
            
            // Try to find ForwardExtentCopy constructor - it may have different signatures in different WE versions
            // Try multiple approaches: find by examining all constructors
            Class<?> extentClass = loadClass("com.sk89q.worldedit.extent.Extent");
            Class<?> regionClass = loadClass("com.sk89q.worldedit.regions.Region");
            
            // Try to find a constructor that matches our needs
            // ForwardExtentCopy typically takes: (source Extent, source Region, dest Extent/clipboard, dest offset BlockVector3)
            java.lang.reflect.Constructor<?>[] constructors = forwardExtentCopyClass.getConstructors();
            forwardExtentCopyConstructor = null;
            
            // First, try exact matches with common signatures
            try {
                // Try: (Extent, Region, BlockArrayClipboard, BlockVector3)
                forwardExtentCopyConstructor = forwardExtentCopyClass.getConstructor(
                    extentClass, regionClass, blockArrayClipboardClass, blockVector3Class);
            } catch (NoSuchMethodException ignored) {
                try {
                    // Try: (Extent, Region, Extent, BlockVector3) - clipboard as Extent interface
                    forwardExtentCopyConstructor = forwardExtentCopyClass.getConstructor(
                        extentClass, regionClass, extentClass, blockVector3Class);
                } catch (NoSuchMethodException ignored2) {
                    // Try: (Extent, Region, BlockArrayClipboard) - 3 params, no offset
                    try {
                        forwardExtentCopyConstructor = forwardExtentCopyClass.getConstructor(
                            extentClass, regionClass, blockArrayClipboardClass);
                    } catch (NoSuchMethodException ignored3) {
                        // Fall back to searching all constructors
                        for (java.lang.reflect.Constructor<?> constructor : constructors) {
                            Class<?>[] paramTypes = constructor.getParameterTypes();
                            if (paramTypes.length >= 3 && paramTypes.length <= 4) {
                                // Check if first two params are Extent and Region
                                if (extentClass.isAssignableFrom(paramTypes[0]) &&
                                    regionClass.isAssignableFrom(paramTypes[1])) {
                                    forwardExtentCopyConstructor = constructor;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            if (forwardExtentCopyConstructor == null) {
                StringBuilder errorMsg = new StringBuilder("Could not find ForwardExtentCopy constructor. Available constructors:\n");
                for (java.lang.reflect.Constructor<?> c : constructors) {
                    errorMsg.append("  ").append(c).append("\n");
                }
                throw new RuntimeException(errorMsg.toString());
            }
            
            // Store parameter count for later use
            forwardExtentCopyParamCount = forwardExtentCopyConstructor.getParameterCount();
            operationsComplete = operationsClass.getMethod("complete", 
                loadClass("com.sk89q.worldedit.function.operation.Operation"));
            clipboardFormatsFindByFile = clipboardFormatsClass.getMethod("findByFile", java.io.File.class);
            clipboardFormatGetReader = clipboardFormatClass.getMethod("getReader", InputStream.class);
            clipboardReaderRead = clipboardReaderClass.getMethod("read");
            builtInClipboardFormatGetPrimaryFileExtension = builtInClipboardFormatClass.getMethod("getPrimaryFileExtension");
            builtInClipboardFormatGetWriter = builtInClipboardFormatClass.getMethod("getWriter", OutputStream.class);
            clipboardWriterWrite = clipboardWriterClass.getMethod("write", clipboardClass);
            
            // ClipboardHolder methods (chained)
            Method createPasteMethod = clipboardHolderClass.getMethod("createPaste", editSessionClass);
            pasteTo = findMethod(createPasteMethod.getReturnType(), "to", blockVector3Class);
            pasteIgnoreAirBlocks = findMethod(createPasteMethod.getReturnType(), "ignoreAirBlocks", boolean.class);
            pasteBuild = findMethod(createPasteMethod.getReturnType(), "build");
            
            available = true;
            return true;
            
        } catch (Exception e) {
            // Log available constructors for debugging
            try {
                java.lang.reflect.Constructor<?>[] allConstructors = forwardExtentCopyClass.getConstructors();
                StringBuilder constructorInfo = new StringBuilder("Available ForwardExtentCopy constructors:\n");
                for (java.lang.reflect.Constructor<?> c : allConstructors) {
                    constructorInfo.append("  ").append(c).append("\n");
                }
                BattleArena.getInstance().warn("Failed to initialize WorldEdit adapter. " + constructorInfo.toString(), e);
            } catch (Exception e2) {
                BattleArena.getInstance().warn("Failed to initialize WorldEdit adapter", e);
            }
            return false;
        }
    }
    
    private Class<?> loadClass(String className) throws ClassNotFoundException {
        return worldEditClassLoader.loadClass(className);
    }
    
    private Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(name) && 
                method.getParameterCount() == paramTypes.length) {
                Class<?>[] actualParams = method.getParameterTypes();
                boolean matches = true;
                for (int i = 0; i < paramTypes.length; i++) {
                    if (!actualParams[i].isAssignableFrom(paramTypes[i])) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    return method;
                }
            }
        }
        throw new RuntimeException("Method not found: " + name);
    }
    
    /**
     * Checks if WorldEdit is available and initialized.
     * 
     * @return true if available
     */
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * Copies a region from one world to another.
     * 
     * @param oldWorld The source world
     * @param newWorld The destination world
     * @param bounds The bounds to copy
     * @return true if successful
     */
    public boolean copyToWorld(World oldWorld, World newWorld, Bounds bounds) {
        if (!available) {
            return false;
        }
        
        try {
            // Create region
            Object minPoint = blockVector3At.invoke(null, bounds.getMinX(), bounds.getMinY(), bounds.getMinZ());
            Object maxPoint = blockVector3At.invoke(null, bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ());
            Object region = cuboidRegionConstructor.newInstance(minPoint, maxPoint);
            
            // Create clipboard
            Object clipboard = blockArrayClipboardConstructor.newInstance(region);
            
            // Create copy operation
            Object adaptedOldWorld = bukkitAdapterAdapt.invoke(null, oldWorld);
            Object copy;
            if (forwardExtentCopyParamCount == 4) {
                // 4 parameters: source, region, clipboard, offset
                copy = forwardExtentCopyConstructor.newInstance(adaptedOldWorld, region, clipboard, minPoint);
            } else if (forwardExtentCopyParamCount == 3) {
                // 3 parameters: source, region, clipboard (offset handled differently or not needed)
                copy = forwardExtentCopyConstructor.newInstance(adaptedOldWorld, region, clipboard);
            } else {
                throw new RuntimeException("Unexpected ForwardExtentCopy constructor parameter count: " + forwardExtentCopyParamCount);
            }
            
            // Execute copy
            operationsComplete.invoke(null, copy);
            
            // Paste to new world
            Object adaptedNewWorld = bukkitAdapterAdapt.invoke(null, newWorld);
            Object worldEditInstance = worldEditGetInstance.invoke(null);
            Object editSession = editSessionNewEditSession.invoke(worldEditInstance, adaptedNewWorld);
            
            // Create paste operation
            Object clipboardHolder = clipboardHolderClass.getConstructor(clipboardClass).newInstance(clipboard);
            Object pasteBuilder = clipboardHolderClass.getMethod("createPaste", editSessionClass).invoke(clipboardHolder, editSession);
            pasteIgnoreAirBlocks.invoke(pasteBuilder, true);
            pasteTo.invoke(pasteBuilder, minPoint);
            Object operation = pasteBuild.invoke(pasteBuilder);
            
            // Execute paste
            operationsComplete.invoke(null, operation);
            
            // Close edit session (if AutoCloseable)
            if (editSession instanceof AutoCloseable) {
                ((AutoCloseable) editSession).close();
            }
            
            return true;
            
        } catch (Exception e) {
            if (worldEditExceptionClass.isInstance(e.getCause())) {
                BattleArena.getInstance().error("Failed to copy region to world", e);
            } else {
                BattleArena.getInstance().error("WorldEdit adapter error", e);
            }
            return false;
        }
    }
    
    /**
     * Creates a schematic from a region.
     * 
     * @param world The world
     * @param bounds The bounds
     * @return The clipboard object, or null on failure
     */
    @Nullable
    public Object createSchematic(World world, Bounds bounds) {
        if (!available) {
            return null;
        }
        
        try {
            Object minPoint = blockVector3At.invoke(null, bounds.getMinX(), bounds.getMinY(), bounds.getMinZ());
            Object maxPoint = blockVector3At.invoke(null, bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ());
            Object region = cuboidRegionConstructor.newInstance(minPoint, maxPoint);
            Object clipboard = blockArrayClipboardConstructor.newInstance(region);
            
            Object adaptedWorld = bukkitAdapterAdapt.invoke(null, world);
            Object copy;
            if (forwardExtentCopyParamCount == 4) {
                // 4 parameters: source, region, clipboard, offset
                copy = forwardExtentCopyConstructor.newInstance(adaptedWorld, region, clipboard, minPoint);
            } else if (forwardExtentCopyParamCount == 3) {
                // 3 parameters: source, region, clipboard (offset handled differently or not needed)
                copy = forwardExtentCopyConstructor.newInstance(adaptedWorld, region, clipboard);
            } else {
                throw new RuntimeException("Unexpected ForwardExtentCopy constructor parameter count: " + forwardExtentCopyParamCount);
            }
            operationsComplete.invoke(null, copy);
            
            return clipboard;
            
        } catch (Exception e) {
            if (worldEditExceptionClass.isInstance(e.getCause())) {
                BattleArena.getInstance().error("Failed to create schematic", e);
            } else {
                BattleArena.getInstance().error("WorldEdit adapter error creating schematic", e);
            }
            return null;
        }
    }
    
    /**
     * Writes a clipboard to a file.
     * 
     * @param clipboard The clipboard object
     * @param path The file path
     * @return true if successful
     */
    public boolean writeSchematic(Object clipboard, Path path) {
        if (!available || clipboard == null) {
            return false;
        }
        
        try {
            Object format = builtInClipboardFormatClass.getField("SPONGE_SCHEMATIC").get(null);
            String extension = (String) builtInClipboardFormatGetPrimaryFileExtension.invoke(format);
            
            try (OutputStream os = java.nio.file.Files.newOutputStream(path)) {
                Object writer = builtInClipboardFormatGetWriter.invoke(format, os);
                clipboardWriterWrite.invoke(writer, clipboard);
            }
            
            return true;
            
        } catch (Exception e) {
            BattleArena.getInstance().error("Failed to write schematic", e);
            return false;
        }
    }
    
    /**
     * Reads a schematic from a file.
     * 
     * @param path The file path
     * @return The clipboard object, or null on failure
     */
    @Nullable
    public Object readSchematic(Path path) {
        if (!available) {
            return null;
        }
        
        try {
            Object format = clipboardFormatsFindByFile.invoke(null, path.toFile());
            if (format == null) {
                return null;
            }
            
            try (InputStream is = java.nio.file.Files.newInputStream(path)) {
                Object reader = clipboardFormatGetReader.invoke(format, is);
                return clipboardReaderRead.invoke(reader);
            }
            
        } catch (Exception e) {
            BattleArena.getInstance().error("Failed to read schematic", e);
            return null;
        }
    }
    
    /**
     * Pastes a clipboard to a world.
     * 
     * @param clipboard The clipboard object
     * @param world The world
     * @param bounds The bounds (min point for paste location)
     * @return true if successful
     */
    public boolean pasteSchematic(Object clipboard, World world, Bounds bounds) {
        if (!available || clipboard == null) {
            return false;
        }
        
        try {
            Object adaptedWorld = bukkitAdapterAdapt.invoke(null, world);
            Object worldEditInstance = worldEditGetInstance.invoke(null);
            Object editSession = editSessionNewEditSession.invoke(worldEditInstance, adaptedWorld);
            
            Object minPoint = blockVector3At.invoke(null, bounds.getMinX(), bounds.getMinY(), bounds.getMinZ());
            Object clipboardHolder = clipboardHolderClass.getConstructor(clipboardClass).newInstance(clipboard);
            Object pasteBuilder = clipboardHolderClass.getMethod("createPaste", editSessionClass).invoke(clipboardHolder, editSession);
            pasteTo.invoke(pasteBuilder, minPoint);
            Object operation = pasteBuild.invoke(pasteBuilder);
            
            operationsComplete.invoke(null, operation);
            
            if (editSession instanceof AutoCloseable) {
                ((AutoCloseable) editSession).close();
            }
            
            return true;
            
        } catch (Exception e) {
            if (worldEditExceptionClass.isInstance(e.getCause())) {
                BattleArena.getInstance().error("Failed to paste schematic", e);
            } else {
                BattleArena.getInstance().error("WorldEdit adapter error pasting schematic", e);
            }
            return false;
        }
    }
    
    /**
     * Gets the primary file extension for sponge schematic format.
     * 
     * @return The file extension (without dot)
     */
    @Nullable
    public String getSpongeSchematicExtension() {
        if (!available) {
            return null;
        }
        
        try {
            Object format = builtInClipboardFormatClass.getField("SPONGE_SCHEMATIC").get(null);
            return (String) builtInClipboardFormatGetPrimaryFileExtension.invoke(format);
        } catch (Exception e) {
            return null;
        }
    }
}
