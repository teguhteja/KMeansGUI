/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ttm.cluster.main;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author palgunadi
 */
class KMeans {
    //ArrayList<Point2D> arrLPoint2D;
    ArrayList<Cluster> arrLCluster;
    ArrayList<Cluster> arrLClusterLast;
    ArrayList<Point2D> arrLPoint2D;
    private final String[] getPointC;
    private double sse;
    int jmlCluster;
    Point2D[] centroid;
    Point2D[] newCentroid;
    boolean isPrintInfo;
    public final int MODE_ORDERED = 0;
    public final int MODE_RAND = 1;
    private int MODE;
    
    public KMeans(ArrayList<Point2D> arrLPoint2D, int jmlCluster){
        //this.arrLPoint2D = arrLPoint2D;
        this.jmlCluster = jmlCluster;
        this.centroid = new Point2D[jmlCluster];
        this.newCentroid = new Point2D[jmlCluster];
        this.arrLPoint2D = arrLPoint2D;
        
        centroid = generateCentroid();
        arrLCluster = new ArrayList<>();
        arrLClusterLast = new ArrayList<>();
        getPointC = new String[arrLPoint2D.size()];
        isPrintInfo = false;
        
        arrLPoint2D.stream().map((p) -> new Cluster(p,"C0")).forEachOrdered((c) -> {
            arrLCluster.add(c);
        });
    }
    
    public void setIsPrintInfo(boolean value){
        isPrintInfo = value;
    }
    
    public void setMode(int Mode){
        this.MODE = Mode;
        centroid = generateCentroid();
    }
    
    boolean isNotSameMemberInCluster() {
        if(arrLClusterLast.isEmpty()){
            return true;
        }else {
            for(int i=0; i < arrLCluster.size();i++){
                Cluster c1 = arrLCluster.get(i);
                Cluster c2 = arrLClusterLast.get(i);
                if(!c1.nameCluster.equals(c2.nameCluster)){
                    return true;
                }
            }
        }
        return false;
    }

    public String[] getPointC(){
        return getPointC;
    }
    
    public double getSse(){
        return sse;
    }
    
    public Point2D[] getCentroid(){
        return centroid;
    }
    
    void proses() {
        arrLClusterLast = (ArrayList<Cluster>) arrLCluster.clone();
        //Collections.copy(arrLClusterLast, arrLCluster);
        int[] memberCentroid = new int[jmlCluster];
        sse = 0.0d;
        
        //calculation euclid
        for(int i=0; i < arrLCluster.size(); i++){
            Cluster c1 = arrLCluster.get(i);
            Double dEuclidean = Double.MAX_VALUE;
            int iCluster = -1;
            for(int j=0; j < jmlCluster; j++){
                Point2D tempC = centroid[j];
                Double tempE = Math.sqrt(Math.pow(c1.p.getX()-tempC.getX(),2.0d)+
                        Math.pow(c1.p.getY()-tempC.getY(),2.0d));
//System.out.println(c1.p.getX()+","+c1.p.getY()+"--"+tempC.getX()+","+tempC.getY()+"="+tempE);                
                if(dEuclidean >= tempE){
                    iCluster = j;
                    dEuclidean = tempE;
                    
                }
            }
            // add new centroid
            Point2D newTempC = newCentroid[iCluster];
            if(newTempC==null){
                newTempC = new Point2D.Double(0.0d, 0.0d);
            }
            newCentroid [iCluster] = new Point2D.Double(newTempC.getX()+c1.p.getX(),
                    newTempC.getY()+c1.p.getY());
            memberCentroid[iCluster]++;
            // count sse
            sse = sse + Math.pow((c1.p.getX() - centroid[iCluster].getX()),2.0d)+ 
                    Math.pow((c1.p.getY() - centroid[iCluster].getY()),2.0d);
            Cluster newC = new Cluster(c1.p, String.valueOf(iCluster+1));
            getPointC[i]=c1.p.getX()+","+c1.p.getY()+" "+iCluster;
            arrLCluster.set(i, newC);
        }
        // calculation new centroid
        for(int j=0; j < jmlCluster; j++){
            Point2D newTempC = newCentroid[j];
            int totalMember = memberCentroid[j];
            newCentroid[j] = new Point2D.Double(newTempC.getX()/totalMember, newTempC.getY()/totalMember);
        }
        
        // print data
        if(isPrintInfo){
            printInfo();
        }
        centroid = Arrays.copyOf(newCentroid, jmlCluster);
    }

    private void printInfo() {
        System.out.println("Centroid yang lama :"+Arrays.toString(centroid));
        
        String temp = "";
        for(Cluster c1 : arrLCluster){
            temp = temp+c1.p.getX()+","+c1.p.getY()+" --> "+c1.nameCluster+"\n";
        }
        System.out.print(temp);
        System.out.println("Nilai SSE :"+sse);
        System.out.println("Centroid yang baru :"+Arrays.toString(newCentroid));
    }

    private Point2D[] generateCentroid() {
        if(MODE == MODE_ORDERED){
            return arrLPoint2D.subList(0, jmlCluster).toArray(centroid);
        }  
        if (MODE == MODE_RAND){
            ArrayList<Integer> list = new ArrayList<>();
            Point2D[] loCentroid = new Point2D[jmlCluster];            
            for(int i=0; i < arrLPoint2D.size(); i++){
                list.add(i);
            }
            Collections.shuffle(list);
            for(int j=0; j < jmlCluster; j++){
                int index = list.get(j);
                loCentroid[j] = arrLPoint2D.get(index);
            }
            return loCentroid;
        }
        return null;
    }
    
    
}
