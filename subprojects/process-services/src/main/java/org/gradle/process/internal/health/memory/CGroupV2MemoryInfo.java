/*
 * (c) Copyright 2023 Palantir Technologies Inc. All rights reserved.
 */

package org.gradle.process.internal.health.memory;

import java.nio.file.Path;

public final class CGroupV2MemoryInfo implements OsMemoryInfo {
    private final CGroupCommonMemoryInfo cGroupCommonMemoryInfo;

    public CGroupV2MemoryInfo(Path mountPath) {
        this.cGroupCommonMemoryInfo = new CGroupCommonMemoryInfo(mountPath, "memory.current", "memory.max");
    }

    @Override
    public OsMemoryStatus getOsSnapshot() {
        return cGroupCommonMemoryInfo.getOsSnapshot();
    }

    Path getMountPath() {
        return cGroupCommonMemoryInfo.getMountPath();
    }
}
