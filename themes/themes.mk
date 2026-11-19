# Resurrected Remix themes: fonts, boot animations, header images, wallpapers,
# looks, UDFPS icons and the default wallpaper and accent.
$(call inherit-product, vendor/extras/themes/fonts/fonts.mk)
$(call inherit-product, vendor/extras/themes/bootanimation/bootanimation.mk)

PRODUCT_PACKAGES += \
    OmniStyle \
    RRWallpapers

# Remix Looks
PRODUCT_PACKAGES += \
    rr_look_crimson_classic \
    rr_look_glass \
    rr_look_mono \
    rr_look_neon_night

# Charging animations
PRODUCT_PACKAGES += \
    MotoChargingAnimationOverlay \
    NothingChargingAnimationOverlay

# Signal and Wi-Fi icon styles
PRODUCT_PACKAGES += \
    AuroraSignalOverlay \
    AuroraWiFiOverlay \
    BentoSignalOverlay \
    BentoWiFiOverlay \
    BlueprintSignalOverlay \
    BlueprintWiFiOverlay \
    CircleSignalOverlay \
    ClassicSignalOverlay \
    ClassicWiFiOverlay \
    CometSignalOverlay \
    CometWiFiOverlay \
    CrystalSignalOverlay \
    CrystalWiFiOverlay \
    DuoSignalOverlay \
    DuoWiFiOverlay \
    GlassSignalOverlay \
    GlassWiFiOverlay \
    HollowSignalOverlay \
    HollowWiFiOverlay \
    LandscapeSignalOverlay \
    LandscapeWiFiOverlay \
    LineDotSignalOverlay \
    LineDotWiFiOverlay \
    LinearSignalOverlay \
    LinearWiFiOverlay \
    NeonSignalOverlay \
    NeonWiFiOverlay \
    OdinSignalOverlay \
    OdinWiFiOverlay \
    OrbitSignalOverlay \
    OrbitWiFiOverlay \
    PlumpySignalOverlay \
    PlumpyWiFiOverlay \
    PulseSignalOverlay \
    PulseWiFiOverlay \
    RemixSignalOverlay \
    RemixWiFiOverlay \
    RetroSignalOverlay \
    RetroWiFiOverlay \
    RomanSignalOverlay \
    RoundSignalOverlay \
    RoundWiFiOverlay \
    SeaSignalOverlay \
    SeaWiFiOverlay \
    ShapesSignalOverlay \
    ShapesWiFiOverlay \
    SleekSignalOverlay \
    SoftSignalOverlay \
    SoftWiFiOverlay \
    StrokeSignalOverlay \
    StrokeWiFiOverlay \
    WavySignalOverlay \
    WavyWiFiOverlay

PRODUCT_PACKAGE_OVERLAYS += vendor/extras/themes/overlay

# UDFPS icons and animations, read by SystemUI
ifeq ($(TARGET_HAS_UDFPS),true)
PRODUCT_PACKAGES += \
    UdfpsAnimations \
    UdfpsIcons
endif
