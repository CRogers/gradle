/*
 * (c) Copyright 2023 Palantir Technologies Inc. All rights reserved.
 */

package org.gradle.process.internal.health.memory;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CGroupMemoryInfoTest2 {
    @Test
    void finds_cgroups_v1_filesystem() {
        Optional<OsMemoryInfo> possibleOsMemoryInfo = CGroupMemoryInfo.findCGroupFilesystemFromMtabLines(List.of(
                "overlay / overlay rw,relatime,"
                        + "lowerdir=/var/lib/docker/231072.231072/overlay2/l/IU7JRIXPBZXUQDMVORLRAADR6Y"
                        + ":/var/lib/docker/231072.231072/overlay2/l/3HAW7CD7RYUZJY76XNOIXCC5TU/var/lib/docker/"
                        + "231072.231072/overlay2/l/OK5ZJH5JRRUIXPSZLVW7J7FSHT,"
                        + "upperdir=/var/lib/docker/231072.231072/overlay2/"
                        + "58b50f70309c7bdbef69824549f25d8eba310bfd42f155d03d5a5402b68ce13d/diff,"
                        + "workdir=/var/lib/docker/231072.231072/overlay2/"
                        + "58b50f70309c7bdbef69824549f25d8eba310bfd42f155d03d5a5402b68ce13d/work 0 0",
                "proc /proc proc rw,nosuid,nodev,noexec,relatime 0 0",
                "tmpfs /dev tmpfs rw,nosuid,size=65536k,mode=755,uid=231072,gid=231072,inode64 0 0",
                "devpts /dev/pts devpts rw,nosuid,noexec,relatime,gid=231077,mode=620,ptmxmode=666 0 0",
                "sysfs /sys sysfs ro,nosuid,nodev,noexec,relatime 0 0",
                "tmpfs /sys/fs/cgroup tmpfs rw,nosuid,nodev,noexec,relatime,mode=755,uid=231072,gid=231072,inode64 0 0",
                "mqueue /dev/mqueue mqueue rw,nosuid,nodev,noexec,relatime 0 0",
                "/dev/root /usr/sbin/docker-init ext4 ro,relatime,discard 0 0",
                "/dev/md0 /etc/resolv.conf ext4 rw,relatime,stripe=512 0 0",
                "shm /dev/shm tmpfs rw,nosuid,nodev,noexec,relatime,size=50331648k,inode64 0 0",
                "tmpfs /mnt/ramdisk tmpfs rw,nodev,relatime,size=50331648k,uid=231072,gid=231072,inode64 0 0",
                "devtmpfs /dev/null devtmpfs rw,relatime,size=129169168k,nr_inodes=32292292,mode=755,inode64 0 0",
                "devpts /dev/console devpts rw,nosuid,noexec,relatime,gid=231077,mode=620,ptmxmode=666 0 0",
                "proc /proc/bus proc ro,nosuid,nodev,noexec,relatime 0 0",
                "tmpfs /proc/acpi tmpfs ro,relatime,uid=231072,gid=231072,inode64 0 0",
                "cgroup /custom/mount/systemd cgroup rw,nosuid,nodev,noexec,relatime,xattr,name=systemd 0 0",
                "cgroup /custom/mount/misc cgroup rw,nosuid,nodev,noexec,relatime,misc 0 0",
                "cgroup /something/else/memory cgroup rw,nosuid,nodev,noexec,relatime,memory 0 0",
                "cgroup /custom/mount/cpu,cpuacct cgroup rw,nosuid,nodev,noexec,relatime,cpu,cpuacct 0 0",
                "cgroup /custom/mount/perf_event cgroup rw,nosuid,nodev,noexec,relatime,perf_event 0 0",
                "cgroup /custom/mount/net_cls,net_prio cgroup rw,nosuid,nodev,noexec,relatime,net_cls,net_prio 0 0",
                "cgroup /custom/mount/freezer cgroup rw,nosuid,nodev,noexec,relatime,freezer 0 0",
                "cgroup /custom/mount/devices cgroup rw,nosuid,nodev,noexec,relatime,devices 0 0",
                "cgroup /custom/mount/pids cgroup rw,nosuid,nodev,noexec,relatime,pids 0 0",
                "cgroup /custom/mount/rdma cgroup rw,nosuid,nodev,noexec,relatime,rdma 0 0",
                "cgroup /custom/mount/cpuset cgroup rw,nosuid,nodev,noexec,relatime,cpuset 0 0",
                "cgroup /custom/mount/blkio cgroup rw,nosuid,nodev,noexec,relatime,blkio 0 0",
                "cgroup /custom/mount/hugetlb cgroup rw,nosuid,nodev,noexec,relatime,hugetlb 0 0",
                "devtmpfs /proc/kcore devtmpfs rw,relatime,size=129169168k,nr_inodes=32292292,mode=755,inode64 0 0",
                "tmpfs /sys/firmware tmpfs ro,relatime,uid=231072,gid=231072,inode64 0 0"));

        assertThat(possibleOsMemoryInfo).hasValueSatisfying(osMemoryInfo -> {
            assertThat(osMemoryInfo).isInstanceOf(CGroupV1MemoryInfo.class);
            assertThat(((CGroupV1MemoryInfo) osMemoryInfo).getMountPath())
                    .isEqualTo(Paths.get("/something/else/memory"));
        });
    }

    @Test
    void finds_cgroups_v2_filesystem() {
        Optional<OsMemoryInfo> possibleOsMemoryInfo = CGroupMemoryInfo.findCGroupFilesystemFromMtabLines(List.of(
                "overlay / overlay rw,relatime,lowerdir=/var/lib/docker/overlay2/l/K3WCX6OX54WGPA7SU6FXEVA7UU:/var/lib/docker/overlay2/l/TXEJ4GBZSLCO5DRYWIKRN7C3IH,upperdir=/var/lib/docker/overlay2/22fc088fbf548578c4b8bbe770d9ae05690a4873a2847feeee7ae09542616bde/diff,workdir=/var/lib/docker/overlay2/22fc088fbf548578c4b8bbe770d9ae05690a4873a2847feeee7ae09542616bde/work 0 0",
                "proc /proc proc rw,nosuid,nodev,noexec,relatime 0 0",
                "tmpfs /dev tmpfs rw,nosuid,size=65536k,mode=755 0 0",
                "devpts /dev/pts devpts rw,nosuid,noexec,relatime,gid=5,mode=620,ptmxmode=666 0 0",
                "sysfs /sys sysfs ro,nosuid,nodev,noexec,relatime 0 0",
                "mqueue /dev/mqueue mqueue rw,nosuid,nodev,noexec,relatime 0 0",
                "shm /dev/shm tmpfs rw,nosuid,nodev,noexec,relatime,size=65536k 0 0",
                "/dev/vda1 /etc/resolv.conf ext4 rw,relatime 0 0",
                "devpts /dev/console devpts rw,nosuid,noexec,relatime,gid=5,mode=620,ptmxmode=666 0 0",
                "proc /proc/bus proc ro,nosuid,nodev,noexec,relatime 0 0",
                "cgroup /another/place/cgroup cgroup2 ro,nosuid,nodev,noexec,relatime 0 0",
                "tmpfs /sys/firmware tmpfs ro,relatime 0 0"));

        assertThat(possibleOsMemoryInfo).hasValueSatisfying(osMemoryInfo -> {
            assertThat(osMemoryInfo).isInstanceOf(CGroupV2MemoryInfo.class);
            assertThat(((CGroupV2MemoryInfo) osMemoryInfo).getMountPath())
                    .isEqualTo(Paths.get("/another/place/cgroup"));
        });
    }

    @Test
    void handles_odd_broken_mtab() {
        Optional<OsMemoryInfo> possibleOsMemoryInfo = CGroupMemoryInfo.findCGroupFilesystemFromMtabLines(
                List.of("only_one_item", "# comment", "", "", "two things"));

        assertThat(possibleOsMemoryInfo).isEmpty();
    }

    @Test
    void handles_empty_mtab() {
        Optional<OsMemoryInfo> possibleOsMemoryInfo = CGroupMemoryInfo.findCGroupFilesystemFromMtabLines(List.of());

        assertThat(possibleOsMemoryInfo).isEmpty();
    }
}
