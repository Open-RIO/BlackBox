package jaci.openrio.module.blackbox;

import jaci.openrio.toast.core.io.Storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A BlackBox Context instance. This class is responsible for Data Reporting to a CSV (Comma Separated Values)
 * file stored under system/blackbox. These values can be imported into Excel, Tableau or any other program that
 * can accept CSV files.
 *
 * Every BlackBox CSV File contains a 'Time' field. The time field contains the amount of Milliseconds that
 * have passed since the context first started writing data. If you are graphing your data, you may use this as the
 * 'X' axis of the line/scatter plot.
 *
 * @author Jaci
 */
public class BlackBoxContext {

    private String name;
    private LinkedHashMap<String, Supplier<Number>> value_suppliers;
    boolean started = false;
    File target_file;
    FileOutputStream file_out;
    long start_time;

    protected BlackBoxContext(String context_name) {
        this.name = context_name;
        value_suppliers = new LinkedHashMap<>();
        target_file = Storage.highestPriority("system/blackbox/" + context_name + ".csv");
        target_file.getParentFile().mkdirs();
        add("Time", () -> System.currentTimeMillis() - start_time);
    }

    /**
     * Get the name of the Context. This, appended with .csv, will give you the Filename of the
     * Context CSV file.
     */
    public String getName() {
        return name;
    }

    /**
     * Add a Value Supplier to this context. The Value Supplier is a callback that is called whenever
     * the Context records data. This is used to gather the current value to record.
     * @param supplier_name The name of the supplier. This may not contain commas.
     * @param value_supplier The supplier to get the Number value from.
     */
    public void add(String supplier_name, Supplier<Number> value_supplier) {
        if (started) throw new IllegalStateException("Cannot add Suppliers after the Context has started!");
        this.value_suppliers.put(supplier_name, value_supplier);
    }

    /**
     * Tick the context. This will cause a single entry to be added to the Context CSV file. This should be
     * called on a regular basis, so it is recommended that this is called either in a Heartbeat Listener,
     * or during a State Tick.
     */
    public void tick() {
        if (!started) {
            // First Run
            started = true;
            start_time = System.currentTimeMillis();
            try {
                file_out = new FileOutputStream(target_file);
                file_out.write(String.join(",", value_suppliers.keySet()).getBytes());
                file_out.write('\n');
            } catch (IOException e) { }
        }

        try {
            file_out.write(String.join(",", value_suppliers.values().stream()
                .map(num -> num.get().toString())
                .collect(Collectors.toList())
            ).getBytes());
            file_out.write('\n');
        } catch (IOException e) { }
    }

}
