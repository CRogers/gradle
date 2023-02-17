/*
 * (c) Copyright 2023 Palantir Technologies Inc. All rights reserved.
 */

package org.gradle.process.internal.health.memory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CGroupCommonMemoryInfo implements OsMemoryInfo {
    private final Path mountPath;
    private final String memoryCurrentFileName;
    private final String memoryMaxFilename;
    private final MemInfoOsMemoryInfo memInfoOsMemoryInfo = new MemInfoOsMemoryInfo();

    public CGroupCommonMemoryInfo(Path mountPath, String memoryCurrentFileName, String memoryMaxFilename) {
        this.mountPath = mountPath;
        this.memoryCurrentFileName = memoryCurrentFileName;
        this.memoryMaxFilename = memoryMaxFilename;
    }

    @Override
    public OsMemoryStatus getOsSnapshot() {
        long usage = readByteFile(memoryCurrentFileName);
        long limit = readByteFile(memoryMaxFilename);

        long machineTotal = memInfoOsMemoryInfo.getOsSnapshot().getTotalPhysicalMemory();

        // When the cgroups limit is greater than total machine memory, there is no limit. No limit is usually a really
        // big number like 9223372036854771712 (8 exabytes)
        long cgroupTotal = Math.min(machineTotal, limit);

        long available = Math.max(0, cgroupTotal - usage);

        return new OsMemoryStatusSnapshot(cgroupTotal, available);
    }

    private long readByteFile(String filename) {
        Path path = mountPath.resolve(filename);

        try {
            String numberOfBytesString = Files.readString(path).trim();

            // cgroups v1 returns PAGE_COUNTER_MAX, which is LONG_MAX / PAGE_SIZE, normally
            //     9223372036854771712 when there is no limit.
            // cgroups v2 returns "max" for when there is no limit.
            if (numberOfBytesString.equals("max")) {
                // If we encounter the cgroups v2 version, make it a really big number greater than the machine memory
                // like cgroups v1.
                return Long.MAX_VALUE;
            }

            return Long.parseLong(numberOfBytesString);
        } catch (IOException e) {
            throw new RuntimeException("Could not read cgroup information from " + path, e);
        }
    }

    public Path getMountPath() {
        return mountPath;
    }
}
