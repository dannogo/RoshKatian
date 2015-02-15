package player.com.roshkatian;

/**
 * Created by oleh on 2/10/15.
 */
public class Stor {
//    private int[] iconId;
    private float[] xPosition;
    private float[] yPosition;
    private String[] iconVisibility;

    public Stor(){
//        this.iconId = new int[10];
        this.xPosition = new float[500];
        this.yPosition = new float[500];
        this.iconVisibility = new String[500];
    }

    public void removePlaylistData(int key){
        for(int i=key+1; i<xPosition.length; i++){

//            xPosition[key+1] = 0.0f;
//            yPosition[key+1] = 0.0f;
//            iconVisibility[key+1] = "";

            this.xPosition[i - 1] = this.xPosition[i];
            this.yPosition[i - 1] = this.yPosition[i];
            this.iconVisibility[i - 1] = this.iconVisibility[i];
        }
//        for ( int i = key ; i < xPosition.length - 1 ; i++ )
//        {
//            xPosition[ i ] = xPosition[ i + 1 ] ;
//            yPosition[ i ] = yPosition[ i + 1 ] ;
//        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String str = "";
        for(int i=0; i<this.size(); i++){
            str += ""+xPosition[i];
            str += " | ";
        }
        return str;
    }

    public void put(int key, float valueX, float valueY){
        xPosition[key] = valueX;
        yPosition[key] = valueY;
    }

    public void putVis(int key, String visibility){
        iconVisibility[key] = visibility;
    }

    public String getVis(int key){
        return iconVisibility[key];
    }

    public float getX(int key){
        return xPosition[key];
    }

    public float getY(int key){
        return yPosition[key];
    }

    public void clear(){
        for(int i=0; i<xPosition.length; i++){
            if(!(xPosition[i] == 0.0 && yPosition[i] == 0.0)){
                xPosition[i] = 0.0f;
                yPosition[i] = 0.0f;
            }else{
                break;
            }
        }
    }

    public int size(){
        int filledCells = 0;
        for(int i=0; i<xPosition.length; i++){
            if(!(xPosition[i] == 0.0 && yPosition[i] == 0.0)){
                filledCells++;
            }else{
                return filledCells;
            }
        }
        return filledCells;
    }

    public boolean isEmpty(){
        if (xPosition[0] == 0.0 && yPosition[0] == 0.0){
            return true;
        }
        return false;
    }


    public boolean containsKey(int key){
        if (key < this.size()){
            return true;
        }
        return false;
    }

}
