package org.lwjgl.util.vma;

import org.lwjgl.system.*;

import static org.lwjgl.system.MemoryUtil.*;

/**
 * Android-patched LWJGL VMA loader.
 *
 * Minecraft 26.2 runs inside Android, so the desktop Linux VMA native
 * must not be extracted and loaded. The launcher supplies an absolute
 * Android-native path through org.lwjgl.vma.libname.
 */
final class LibVma {
    static {
        String defaultName = Platform.mapLibraryNameBundled("lwjgl_vma");
        String configuredName = System.getProperty("org.lwjgl.vma.libname", defaultName);
        Library.loadSystem(System::load, System::loadLibrary, LibVma.class, "org.lwjgl.vma", configuredName);

        MemoryAllocator allocator = getAllocator(Configuration.DEBUG_MEMORY_ALLOCATOR_INTERNAL.get(true));
        setupMalloc(
            allocator.getMalloc(),
            allocator.getCalloc(),
            allocator.getRealloc(),
            allocator.getFree(),
            allocator.getAlignedAlloc(),
            allocator.getAlignedFree()
        );
    }

    private LibVma() {
    }

    static void initialize() {
        // Trigger static initialization.
    }

    private static native void setupMalloc(
        long malloc,
        long calloc,
        long realloc,
        long free,
        long aligned_alloc,
        long aligned_free
    );
}
