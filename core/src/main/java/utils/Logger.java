package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {

    private static final String[] RAM_UNITS = {"b", "kb", "mb"};
    public static final Logger out = new Logger();

    private boolean debug, verbose, information = true, normal = true, memory = true, time = true;
    private boolean file_output = false;
    private String str, prestr;

    private final long timer;
    private final Runtime rt;
    private PrintWriter writer;

    private Logger(){
        this.timer = System.currentTimeMillis();
        this.rt = Runtime.getRuntime();
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setInformation(boolean information) {
        this.information = information;
    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

    public void setMemory(boolean memory) {
        this.memory = memory;
    }

    public void setTime(boolean time) {
        this.time = time;
    }

    public void setFileOutput(boolean file_output){
        this.file_output = file_output;
        if(file_output && writer == null) {
            try {
                writer = new PrintWriter(new FileWriter("log.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void debug(Object object){
        if(debug) print("[DEBUG] ", object);
    }

    public void verbose(Object object){
        if(verbose) print("[VERBOSE] ", object);
    }

    public void print(Object object){
        if(normal) print("", object);
    }

    public void information(Object object){
        if(information) print("[INFORMATION] ", object);
    }

    private void print(String type, Object object){
        str = object.toString();
        adapt();
        str = ("\r"+prestr + type + str);
        System.out.print(str);
        if(file_output) {
            try {
                writer = new PrintWriter(new FileWriter("log.txt"));
                writer.write(str);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                writer.close();
            }
        }
    }

    private void adapt(){
        prestr = "";
        if(str.startsWith("\n")) {
            prestr += "\n";
            str = str.substring(1);
        }
        if(str.startsWith("\r")) {
            prestr += "\r";
            str = str.substring(1);
        }
        if(str.startsWith("\n")) {
            prestr += "\n";
            str = str.substring(1);
        }
        if(time) prestr += time();
        if(memory) prestr += ram();
    }

    private String ram(){
        long mem = rt.totalMemory() - rt.freeMemory();
        String unit = RAM_UNITS[0];
        for(int i = 1; i < RAM_UNITS.length; i++){
            if(mem > 1024) {
                mem /= 1024.0;
                unit = RAM_UNITS[i];
            } else break;
        }
        return "["+pad(5, String.valueOf(mem))+unit+"] ";
    }

    private String time(){
        double time = System.currentTimeMillis() - timer;
        int m = 0, s = 0, ms = 0;
        if (time > 1000) {
            s = (int) Math.floor(time / 1000);
            ms = (int) (time - s*1000);
        } if (s > 60){
            m = s / 60;
            s -= m * 60;
        }
        return "["+pad(2, String.valueOf(m))+":"+pad(2, String.valueOf(s))+":"+pad(3, String.valueOf(ms))+"] ";
    }

    private String pad(int length, String inputString){
        return String.format("%1$" + length + "s", inputString).replace(' ', '0');
    }
}
