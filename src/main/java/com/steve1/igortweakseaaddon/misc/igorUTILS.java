package com.steve1.igortweakseaaddon.misc;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.base_atmospheric_pressure;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class igorUTILS {
    public static double sanitize_number(double number,double deafult) {
        if (Double.isNaN(number)||Double.isInfinite(number)) {
            logger.warn("number got sanitized! We got: "+number+", with deafult: "+deafult);
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
        double abs_val=Math.abs(pressure);
        if (abs_val >= 1000000.0) {
            return String.format("%.2f MPa", pressure / 1000000.0);
        } else if (abs_val >= 1000.0) {
            return String.format("%.2f kPa", pressure / 1000.0);
        } else if (abs_val >= 1) {
            return String.format("%.2f Pa", pressure);
        } else if (abs_val >= 0.001) {
            return String.format("%.2f kPa", pressure * 1000);
        } else {
            return String.format("%.2f µPa", pressure * 1000000);
        }
    }

    public static String plot_atmospheres(double pressure) {
        double abs_val=Math.abs(pressure);
        if (abs_val >= base_atmospheric_pressure *100) {
            return String.format("%.0f Atm", pressure/ base_atmospheric_pressure);
        } else if (abs_val >= base_atmospheric_pressure *10) {
            return String.format("%.1f Atm", pressure/ base_atmospheric_pressure);
        } else if (abs_val >= base_atmospheric_pressure *0.95) {
            return String.format("%.2f Atm", pressure/ base_atmospheric_pressure);
        } else if (abs_val >= base_atmospheric_pressure *0.1) {
            return String.format("%.3f Atm", pressure/ base_atmospheric_pressure);
        } else if (abs_val >= base_atmospheric_pressure /1000) {
            return String.format("%.2f mAtm", 1000*pressure/ base_atmospheric_pressure);
        } else {
            return String.format("%.2f µAtm", 1000000*pressure/ base_atmospheric_pressure);
        }
    }

    public static String plot_speed(double speed) {
        double abs_val=Math.abs(speed);
        if (abs_val >= 1000.0) {
            return String.format("%.2f KM/S", speed / 1000.0);
        } else if (abs_val>=1) {
            return String.format("%.2f M/S", speed);
        } else if (abs_val>=0.01) {
            return String.format("%.2f SM/S", speed * 100);
        } else if (abs_val>=0.001) {
            return String.format("%.2f MM/S", speed * 1000);
        } else {
            return String.format("%.2f µM/S", speed * 1000000);
        }
    }

    public static String plot_percent(double value) {
        value*=100;
        double abs_val=Math.abs(value);
        if (abs_val>=10) {
            return String.format("%.1f", value) + " %";
        } else if (abs_val>=1) {
            return String.format("%.2f", value) + " %";
        } else {
            return String.format("%.3f", value) + " %";
        }
    }

    public static String plot_area(double value) {
        double abs_val=Math.abs(value);
        if (abs_val>=1) {
            return String.format("%.2f M²", value);
        } else if (abs_val>=0.1) {
            return String.format("%.3f M²", value);
        } else if (abs_val>=1e-4) {
            return String.format("%.2f sm²", value*1e4);
        } else if (abs_val>=1e-5) {
            return String.format("%.3f sm²", value*1e4);
        } else if (abs_val>=1e-6) {
            return String.format("%.4f sm²", value*1e4);
        } else if (abs_val>=1e-9) {
            return String.format("%.2f mm²", value*1e9);
        } else {
            return String.format("%.3f mm²", value*1e9);
        }
    }

    public static ItemStack get_stack_in_slot(IInventory inventory, int slot) {
        return inventory.getStackInSlot(slot);
    }
}
