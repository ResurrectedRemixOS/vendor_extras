# Resurrected Remix themes: fonts, boot animations, header images, wallpapers,
# UDFPS icons and the default wallpaper and accent.
$(call inherit-product, vendor/extras/themes/fonts/fonts.mk)
$(call inherit-product, vendor/extras/themes/bootanimation/bootanimation.mk)

PRODUCT_PACKAGES += \
    LivingCrimson \
    OmniStyle \
    RRWallpapers

PRODUCT_PACKAGE_OVERLAYS += vendor/extras/themes/overlay

# UDFPS icons and animations, read by SystemUI
ifeq ($(TARGET_HAS_UDFPS),true)
PRODUCT_PACKAGES += \
    UdfpsAnimations \
    UdfpsIcons
endif
