package mage.client.util.gui.countryBox;

import mage.client.util.GUISizeHelper;
import mage.client.util.ImageHelper;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LevelX2
 */
public final class CountryUtil {

    private static final Logger LOGGER = Logger.getLogger(CountryUtil.class);
    private static final Map<String, ImageIcon> FLAG_ICON_CACHE = new HashMap<>();
    private static final Map<String, String> COUNTRY_MAP = new HashMap<>();

    public static ImageIcon getCountryFlagIcon(String countryCode) {
        ImageIcon flagIcon = FLAG_ICON_CACHE.get(countryCode);
        if (flagIcon == null) {
            Image flagImage = ImageHelper.getImageFromResources("/flags/" + countryCode + (countryCode.endsWith(".png") ? "" : ".png"));
            if (flagImage != null) {
                if (GUISizeHelper.tableFlagHeight > 11) {
                    int width = Math.round(GUISizeHelper.tableFlagHeight * flagImage.getWidth(null) / flagImage.getHeight(null));
                    BufferedImage resized = ImageHelper.scale((BufferedImage) flagImage, BufferedImage.TYPE_4BYTE_ABGR, width, GUISizeHelper.tableFlagHeight);
                    flagIcon = new ImageIcon(resized);
                } else {
                    flagIcon = new ImageIcon(flagImage);
                }
            }
            if (flagIcon == null || flagIcon.getImage() == null) {
                LOGGER.warn("Country flag resource not found: " + countryCode);
                FLAG_ICON_CACHE.put(countryCode, flagIcon);
            } else {
                FLAG_ICON_CACHE.put(countryCode, flagIcon);
            }
        }
        return flagIcon;
    }

    public static void changeGUISize() {
        FLAG_ICON_CACHE.clear();
    }

    public static String getCountryName(String countryCode) {
        if (COUNTRY_MAP.isEmpty()) {
            for (int i = 0; i < CountryComboBox.countryList.length; i++) {
                COUNTRY_MAP.put(CountryComboBox.countryList[i][1], CountryComboBox.countryList[i][0]);
            }
        }
        return COUNTRY_MAP.get(countryCode);
    }
}
