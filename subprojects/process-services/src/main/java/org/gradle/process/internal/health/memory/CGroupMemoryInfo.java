/*
 * (c) Copyright 2023 Palantir Technologies Inc. All rights reserved.
 */

package org.gradle.process.internal.health.memory;

import com.google.common.base.Splitter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

final class CGroupMemoryInfo {
    public static Optional<OsMemoryInfo> findCGroupMemoryInfo() {
        return parseFromMtab().or(() -> {
            Path defaultCGroupMountPath = Paths.get("/sys/fs/cgroup");

            // cgroups v2 has cgroup.controllers, cgroups v1 does not
            if (Files.exists(defaultCGroupMountPath.resolve("cgroup.controllers"))) {
                return Optional.of(new CGroupV2MemoryInfo(defaultCGroupMountPath));
            }

            Path defaultCGroupV1MemoryMountPath = defaultCGroupMountPath.resolve("memory");

            if (Files.exists(defaultCGroupV1MemoryMountPath) && Files.isDirectory(defaultCGroupV1MemoryMountPath)) {
                return Optional.of(new CGroupV1MemoryInfo(defaultCGroupV1MemoryMountPath));
            }

            return Optional.empty();
        });
    }

    private static Optional<OsMemoryInfo> parseFromMtab() {
        Path etcMtab = Paths.get("/etc/mtab");

        if (Files.notExists(etcMtab)) {
            return Optional.empty();
        }

        try {
            return findCGroupFilesystemFromMtabLines(Files.readAllLines(etcMtab));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    static Optional<OsMemoryInfo> findCGroupFilesystemFromMtabLines(List<String> mtabLines) {
        for (String mtabLine : mtabLines) {
            List<String> parts = Splitter.on(' ').splitToList(mtabLine);

            if (parts.size() < 3) {
                return Optional.empty();
            }

            Path mountPath = Paths.get(parts.get(1));
            String filesSystemType = parts.get(2);
            String mountOptions = parts.get(3);

            if (filesSystemType.equals("cgroup")) {

                boolean isMemoryCGroupMount =
                        Splitter.on(',').splitToStream(mountOptions).anyMatch(Predicate.isEqual("memory"));

                if (!isMemoryCGroupMount) {
                    continue;
                }

                return Optional.of(new CGroupV1MemoryInfo(mountPath));
            }

            if (filesSystemType.equals("cgroup2")) {
                return Optional.of(new CGroupV2MemoryInfo(mountPath));
            }
        }

        return Optional.empty();
    }

    private CGroupMemoryInfo() {}
}
