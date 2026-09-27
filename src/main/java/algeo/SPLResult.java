package algeo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SPLResult {
    public enum Jenis { UNIQUE, NONE, INFINITE }

    public Jenis jenis;
    public double[] konstanta;
    public List<Map<String, Double>> koefisienParam;
    public List<String> namaParameter;
    public List<String> langkah;
    public String namaMetode;

    public SPLResult() {
        this.langkah = new ArrayList<>();
    }

    public static String namaParameterKe(int index) {
        String[] dasar = {"s", "t", "u", "v", "w"};
        if(index<5){
            return dasar[index];
        }
        return "a" + (index-4);
    }

    public String variabelToString(int indexVariabel, int totalVariabel) {
        if(this.jenis==Jenis.NONE){
            return "solusi tidak ada";
        }
        if(this.jenis==Jenis.UNIQUE){
            return "x" + (indexVariabel+1) + " = " + IOHandler.formatNumber(this.konstanta[indexVariabel]);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("x").append(indexVariabel+1).append(" = ");
        
        double c = this.konstanta[indexVariabel];
        boolean hasParam = false;
        
        if(this.koefisienParam!=null && indexVariabel<this.koefisienParam.size() && this.koefisienParam.get(indexVariabel)!=null && this.namaParameter!=null){
            for(int i=0; i<this.namaParameter.size(); i++){
                String nama = this.namaParameter.get(i);
                Double koef = this.koefisienParam.get(indexVariabel).get(nama);
                if(koef!=null && koef!=0){
                    hasParam = true;
                    break;
                }
            }
        }
        
        boolean isFirst = true;
        if(c!=0 || !hasParam){
            sb.append(IOHandler.formatNumber(c));
            isFirst = false;
        }
        
        if(hasParam){
            for(int i=0; i<this.namaParameter.size(); i++){
                String nama = this.namaParameter.get(i);
                Double koef = this.koefisienParam.get(indexVariabel).get(nama);
                if(koef==null || koef==0){
                    continue;
                }
                
                if(koef>0){
                    if(!isFirst){
                        sb.append(" + ");
                    }
                } else {
                    if(!isFirst){
                        sb.append(" - ");
                    } else {
                        sb.append("-");
                    }
                }
                
                double absKoef = Math.abs(koef);
                if(absKoef!=1.0){
                    sb.append(IOHandler.formatNumber(absKoef));
                }
                sb.append(nama);
                isFirst = false;
            }
        }
        
        return sb.toString();
    }

    public String toDisplayString(int totalVariabel) {
        if(this.jenis==Jenis.NONE){
            return "solusi tidak ada";
        }
        
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<totalVariabel; i++){
            sb.append(variabelToString(i, totalVariabel));
            if(i<totalVariabel-1){
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
