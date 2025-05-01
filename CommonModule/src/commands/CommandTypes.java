package commands;

import java.io.Serializable;

public enum CommandTypes implements Serializable {

    Add("add"),//
    Clear("clear"),//
    Exit("exit"),//
    Info("info"),//
    RemoveById("remove_by_id"),//
    RemoveLower("remove_lower"),//
    Show("show"),//
    Update("update"),//
    Help("help"),//
    Save("save"),//
    ExecuteScript("execute_script"),//
    Reorder("reorder"),//
    FilterByMetersAboveSeaLevel("filter_by_meters_above_sea_level"),
    MaxByPopulation("max_by_population"),//
    PrintFieldDescendingMetersAboveSeaLevel("print_field_descending_meters_above_sea_level"),//
    RemoveFirst("remove_first");//
    private String type;
    private static final long serialVersionUID = 1L;

    private CommandTypes(String type) {
        this.type = type;
    }

    public String Type() {
        return type;
    }

    public static CommandTypes getByString(String mes){
        String[] line;
        try{
            line = mes.split("_");
            if(line.length==1) return CommandTypes.valueOf(mes.toUpperCase().charAt(0) + mes.toLowerCase().substring(1));
            StringBuilder sb = new StringBuilder();
            for (String e: line){
                sb.append(e.toUpperCase().charAt(0) + e.toLowerCase().substring(1));
            }
            return CommandTypes.valueOf(sb.toString());
        }
        catch (NullPointerException | IllegalArgumentException e){
        }
        return null;
    }
}