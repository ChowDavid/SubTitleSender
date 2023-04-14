package au.david.church.subtitle;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class SubTitleDto {
    private int id;
    private String subtitle;
    private String head;
    private String meta;


    public SubTitleDto(String line,int width) {
        String[] cells = line.split("\\|");
        id = Integer.parseInt(cells[0]);
        if (cells.length==3){
            if (cells[1].equalsIgnoreCase("h")){
                head=cells[2];
            } else if (cells[1].equalsIgnoreCase("m")){
                meta=cells[2];
            }
        } else {
            int main=cells[1].length();
            int both = (int)Math.floor((width-main)/2*2.5);
            String side = "                                                               ".substring(0,both);
            //subtitle = side+cells[1];
            subtitle = cells[1];
        }
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                "->" + subtitle + '\'' +
                '}';
    }
}
