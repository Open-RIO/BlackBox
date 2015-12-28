package jaci.openrio.module.blackbox;

import jaci.openrio.toast.lib.module.ToastModule;

public class BlackBox extends ToastModule {

    @Override
    public String getModuleName() {
        return "BlackBox";
    }

    @Override
    public String getModuleVersion() {
        return "0.0.1";
    }

    @Override
    public void prestart() { }

    @Override
    public void start() { }

    /**
     * Create a BlackBox context instance
     * @param context_name The name of the context. This appended with .csv will yield
     *                     the filename of the recording file.
     */
    public static BlackBoxContext context(String context_name) {
        return new BlackBoxContext(context_name);
    }
}
