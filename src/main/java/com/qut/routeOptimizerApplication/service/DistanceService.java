package com.qut.routeOptimizerApplication.service;

import org.springframework.web.servlet.ModelAndView;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.shapes.GHPoint;
import com.qut.routeOptimizerApplication.Bean.Location;
import com.qut.routeOptimizerApplication.Bean.UploadInvoiceBean;
import com.qut.routeOptimizerApplication.properties.RouteOptimzerProperties;

public class DistanceService {
public double getDistance(Location source,Location destination) {
	GHPoint ghPointSource,ghPointDestination;
	GHResponse ghResponse=new GHResponse();
	
	RouteOptimzerProperties routeOptimzerProperties=new RouteOptimzerProperties();
	GraphHopper graphHopper = new GraphHopper().setGraphHopperLocation(routeOptimzerProperties.hopperDirectory)
			.setEncodingManager(new EncodingManager("car")).setOSMFile(routeOptimzerProperties.osmFilePath);
	graphHopper.importOrLoad();
	GHRequest request = new GHRequest();
	ghPointSource=new GHPoint();
	ghPointSource.lat=Double.parseDouble(source.getLatitude());
	ghPointSource.lon=Double.parseDouble(source.getLongitude());
	ghPointDestination=new GHPoint();
	ghPointDestination.lat=Double.parseDouble(destination.getLatitude());
	ghPointDestination.lon=Double.parseDouble(destination.getLongitude());
	request.addPoint(ghPointSource);
	request.addPoint(ghPointDestination);
	request.putHint("calcPoints", false);
	request.putHint("instructions", true);
	request.setVehicle("car");
	System.out.println("request"+request.toString());
	ghResponse = graphHopper.route(request);
	System.out.println("response"+ghResponse.toString());
	/*if (ghResponse.getInstructions() != null) {
		for (Instruction i : ghResponse.getInstructions()) {
			s += "------>\ntime <long>: " + i.getTime() + "\n" + "name: street name" + i.getName() + "\n"
					+ "annotation <InstructionAnnotation>" + i.getAnnotation() + "\n" + "distance" + i.getDistance()
					+ "\n" + "sign <int>:" + i.getSign() + "\n" + "Points <PointsList>: " + i.getPoints() + "\n";
		}}*/
	
	
	return ghResponse.getDistance();
}
public ModelAndView calculateDistanceMatrix(UploadInvoiceBean uploadInvoiceBean) {
	ModelAndView model=new ModelAndView("index");
	int locationLength=uploadInvoiceBean.getLocationList().size();
	double[][] distanceArray = new double[locationLength][locationLength];
	Location source;
	Location destination;
	 for(int i=0;i<locationLength;i++) {
		 source=uploadInvoiceBean.getLocationList().get(i);
		 for(int j=0;j<locationLength;j++) {
			 destination= uploadInvoiceBean.getLocationList().get(j);
			 if(i==j)
				 distanceArray[i][j]=0;
			 else
			 distanceArray[i][j]=getDistance(source, destination);
		 }
	 }
	 for(int i=0;i<locationLength;i++) {
		 for(int j=0;j<locationLength;j++) {
			 System.out.print(distanceArray[i][j] + " ");
		 }
		 System.out.println();
	 }
	model.addObject("distanceArray", distanceArray);
	return model;
}
}