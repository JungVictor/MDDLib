package utils;

import java.util.HashMap;

public class ArgumentParser {

    private HashMap<String, String> arguments;

    public ArgumentParser(String... default_args){
        this.arguments = new HashMap<>();
        for(int i = 0; i < default_args.length; i++) {
            int name_pos = i;
            int end_arg = i;
            StringBuilder args = new StringBuilder();
            if(default_args[i].charAt(0) == '-') name_pos = i;
            while (end_arg+1 < default_args.length && default_args[end_arg+1].charAt(0) != '-') args.append(default_args[++end_arg]);
            arguments.put(default_args[name_pos], args.toString());
            i = end_arg;
        }
    }

    public void read(String[] args){
        for(int i = 0; i < args.length; i++)
            if(args[i].charAt(0) == '-') {
                int end_arg = i;
                StringBuilder builder = new StringBuilder();
                while (end_arg+1 < args.length && args[end_arg+1].charAt(0) != '-') builder.append(args[++end_arg]);
                arguments.replace(args[i],  builder.toString());
                i = end_arg;
            }
    }

    public String get(String key){
        return arguments.get(key);
    }

}
