package com.steve1.igortweakseaaddon.misc;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.base_atmospheric_pressure;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class igorUTILS {
    public static double sanitize_number(double number,double deafult) {
        if (Double.isNaN(number)||Double.isInfinite(number)) {
            //logger.warn("number got sanitized! We got: "+number);
            return deafult;
        }
        return number;
    }

    public static void check_for_nan(double number,String str) {
        if (Double.isNaN(number)) {
            logger.error("Got nan! Found in "+str);
        }
    }

    public static double sanitize_number(double number) {
        return sanitize_number(number,0);
    }

    public static String plot_pascals_atmospheres(double pressure) {
        return plot_pascals(pressure)+" "+plot_atmospheres(pressure);
    }

    public static String plot_pascals(double pressure) {
        if (pressure >= 1000000.0) {
            return String.format("%.2f MPa", pressure / 1000000.0);
        } else if (pressure >= 1000.0) {
            return String.format("%.2f kPa", pressure / 1000.0);
        } else if (pressure >= 1) {
            return String.format("%.2f Pa", pressure);
        } else if (pressure >= 0.001) {
            return String.format("%.2f kPa", pressure * 1000);
        } else {
            return String.format("%.2f µPa", pressure * 1000000);
        }
    }

    public static String plot_atmospheres(double pressure) {
        if (pressure >= base_atmospheric_pressure *100) {
            return String.format("%.0f Atm", pressure/ base_atmospheric_pressure);
        } else if (pressure >= base_atmospheric_pressure *10) {
            return String.format("%.1f Atm", pressure/ base_atmospheric_pressure);
        } else if (pressure >= base_atmospheric_pressure *0.95) {
            return String.format("%.2f Atm", pressure/ base_atmospheric_pressure);
        } else if (pressure >= base_atmospheric_pressure *0.1) {
            return String.format("%.3f Atm", pressure/ base_atmospheric_pressure);
        } else if (pressure >= base_atmospheric_pressure /1000) {
            return String.format("%.2f mAtm", 1000*pressure/ base_atmospheric_pressure);
        } else {
            return String.format("%.2f µAtm", 1000000*pressure/ base_atmospheric_pressure);
        }
    }

    public static String plot_speed(double speed) {
        if (speed >= 1000.0) {
            return String.format("%.2f KM/S", speed / 1000.0);
        } else if (speed>=1) {
            return String.format("%.2f M/S", speed);
        } else if (speed>=0.01) {
            return String.format("%.2f SM/S", speed * 100);
        } else if (speed>=0.001) {
            return String.format("%.2f MM/S", speed * 1000);
        } else {
            return String.format("%.2f µM/S", speed * 1000000);
        }
    }
}
