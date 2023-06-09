package bdbt_project.SpringApplication.dbtables;


public class Adres {
    private int nr_adresu;
    private String kraj;
    private String miasto;
    private String kod_pocztowy;
    private String ulica;
    private String nr_domu;
    private Integer nr_mieszkania;

    public Adres(){

    }

    public Adres(Integer nr_adresu, String kraj,
                 String miasto, String kod_pocztowy,
                 String ulica, String nr_domu,
                 Integer nr_mieszkania) {
        this.nr_adresu = nr_adresu;
        this.kraj = kraj;
        this.miasto = miasto;
        this.kod_pocztowy = kod_pocztowy;
        this.ulica = ulica;
        this.nr_domu = nr_domu;
        this.nr_mieszkania = nr_mieszkania;
    }

    public int getNr_adresu() {
        return nr_adresu;
    }
    public void setNr_adresu(int nr_adresu) {
        this.nr_adresu = nr_adresu;
    }

    public String getKraj() {
        return kraj;
    }
    public void setKraj(String kraj) {
        this.kraj = kraj;
    }

    public String getMiasto() {
        return miasto;
    }
    public void setMiasto(String miasto) {
        this.miasto = miasto;
    }

    public String getKod_pocztowy() {
        return kod_pocztowy;
    }
    public void setKod_pocztowy(String kod_pocztowy) {
        this.kod_pocztowy = kod_pocztowy;
    }

    public String getUlica() {
        return ulica;
    }
    public void setUlica(String ulica) {
        this.ulica = ulica;
    }

    public String getNr_domu() {
        return nr_domu;
    }
    public void setNr_domu(String nr_domu) {
        this.nr_domu = nr_domu;
    }

    public Integer getNr_mieszkania() {
        return nr_mieszkania;
    }
    public void setNr_mieszkania(Integer nr_mieszkania) {
        this.nr_mieszkania = nr_mieszkania;
    }

    public String getKod_pocztowy_formatted() {
        var split = this.kod_pocztowy.split("");
        return split[0]+split[1]+"-"+split[2]+split[3]+split[4];
    }

    public String getUlica_formatted() {
        var split = this.miasto.split(" ");
        StringBuilder formatted = new StringBuilder();
        for(var elem: split) {
            formatted.append(elem);
            formatted.append("+");
        }
        return formatted.toString();
    }

    public String getNr_mieszkania_formatted() {
        if(nr_mieszkania == null) return "-";
        return String.valueOf(nr_mieszkania);
    }

    @Override
    public String toString() {
        return "Adres{" +
                "nr_adresu=" + nr_adresu +
                ", kraj='" + kraj + '\'' +
                ", miasto='" + miasto + '\'' +
                ", kod_pocztowy='" + kod_pocztowy + '\'' +
                ", ulica='" + ulica + '\'' +
                ", nr_domu='" + nr_domu + '\'' +
                ", nr_mieszkania=" + nr_mieszkania +
                '}';
    }
}
