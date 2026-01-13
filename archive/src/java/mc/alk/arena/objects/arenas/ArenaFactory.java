package mc.alk.arena.objects.arenas;

/**
 *
 *
 * @author Nikolai
 */
public interface ArenaFactory {

    public Arena newArena();

    public static final ArenaFactory DEFAULT = new ArenaFactory() {

        @Override
        public Arena newArena() {
            return new Arena();
        }
    };

}
