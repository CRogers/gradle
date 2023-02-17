/*
 * (c) Copyright 2023 Palantir Technologies Inc. All rights reserved.
 */

package org.gradle.process.internal.health.memory;

import java.nio.file.Path;

public final class CGroupV1MemoryInfo implements OsMemoryInfo {
    private final CGroupCommonMemoryInfo cGroupCommonMemoryInfo;

    public CGroupV1MemoryInfo(Path mountPath) {
        this.cGroupCommonMemoryInfo =
                new CGroupCommonMemoryInfo(mountPath, "memory.usage_in_bytes", "memory.limit_in_bytes");
    }

    @Override
    public OsMemoryStatus getOsSnapshot() {
        return cGroupCommonMemoryInfo.getOsSnapshot();
    }

    Path getMountPath() {
        return cGroupCommonMemoryInfo.getMountPath();
    }
}
