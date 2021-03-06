SUMMARY = "NNStreamer, Stream Pipeline Paradigm for Nerual Network Applications"
DESCRIPTION = "NNStreamer is a GStreamer plugin allowing to construct neural network applications with stream pipeline paradigm."
SECTION = "AI"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "\
                file://LICENSE;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                file://debian/copyright;md5=0462ef8fa89a1f53f2e65e74940519ef \
                "

DEPENDS = "orc-native glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base gtest"
DEPENDS += "\
        ${@bb.utils.contains('DISTRO_FEATURES','tensorflow-lite','tensorflow-lite','',d)} \
        "
DEPENDS += "python python-numpy python3 python3-numpy"

SRC_URI = "\
        git://github.com/nnsuite/nnstreamer.git;protocol=https \
        file://0001-Test-Common-Remove-a-unit-test-for-custom-configurat.patch \
        "

PV = "1.0.0+git${SRCPV}"
SRCREV = "720ce6ec7f68325dfd1e6c2ded6024e805f3eff2"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON += "\
                -Denable-orc=true \
                -Denable-test=true \
                -Dinstall-test=true \
                -Denable-pytorch=false \
                -Denable-caffe2=false \
                -Dinstall-example=true \
                -Ddisable-audio-support=false \
                "

PACKAGECONFIG ??= "\
                ${@bb.utils.contains('DISTRO_FEATURES','opencv','opencv','',d)} \
                ${@bb.utils.contains('DISTRO_FEATURES','tensorflow','tensorflow','',d)} \
                ${@bb.utils.contains('DISTRO_FEATURES','tensorflow-lite','tensorflow-lite','',d)} \
                "

PACKAGECONFIG[tensorflow] = "-Denable-tensorflow=true,-Denable-tensorflow=false,tensorflow"
PACKAGECONFIG[tensorflow-lite] = "-Denable-tensorflow-lite=true,-Denable-tensorflow-lite=false,tensorflow-lite"

do_install_append() {
    (cd ${D}/${libdir}; ln -s ./gstreamer-1.0/libnnstreamer.so)
}
INSANE_SKIP_${PN} += "dev-so"

FILES_${PN} += "\
            ${libdir}/*.so \
            ${libdir}/gstreamer-1.0/*.so \
            ${libdir}/nnstreamer/decoders/* \
            ${sysconfdir}/nnstreamer.ini \
            "

PACKAGES =+ "${PN}-unittest ${PN}-tensorflow-lite"

FILES_${PN}-unittest += "\
                    ${libdir}/nnstreamer/customfilters/* \
                    ${libdir}/nnstreamer/unittest/* \
                    "

FILES_${PN}-tensorflow-lite += "\
                            ${@bb.utils.contains('DISTRO_FEATURES','tensorflow-lite', \
                               '${libdir}/nnstreamer/filters/libnnstreamer_filter_tensorflow-lite.so','',d)} \
                            "

RDEPENDS_${PN}-unittest = "nnstreamer gstreamer1.0-plugins-good ssat"
RDEPENDS_${PN}-unittest += "python python-numpy python-numbers python-unittest python-misc"
RDEPENDS_${PN}-unittest += "\
                        ${@bb.utils.contains('DISTRO_FEATURES','tensorflow-lite', \
                            '${PN}-tensorflow-lite','',d)} \
                        "

RDEPENDS_${PN} = "glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base"
RDEPENDS_${PN} += "python python-numpy python-math"
RDEPENDS_${PN} += "python3 python3-numpy python3-math"

FILES_${PN}-dev = "\
                ${includedir}/nnstreamer/* \
                ${libdir}/*.a \
                ${libdir}/pkgconfig/nnstreamer.pc \
                "
