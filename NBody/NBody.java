public class NBody {
  public static void main(String[] args) {
    double T = Double.parseDouble(args[0]);
    double dt = Double.parseDouble(args[1]);
    String filename = args[2];
    double universeradius = readRadius(filename);
    Planet[] planets = readPlanets(filename);
    StdDraw.setScale(((-1)*universeradius),universeradius);
    StdDraw.picture(0,0,"./images/starfield.jpg");
    int planet_counter = 0;
    while(planet_counter<planets.length){
      planets[planet_counter].draw();
      planet_counter+=1;
    }
    int timecounter = 0;
	  while (timecounter<T) {
	    double[] xForces = new double[planets.length];
	  	double[] yForces = new double[planets.length];
	  	planet_counter = 0;
    	while (planet_counter < planets.length) {
	    	xForces[planet_counter] = planets[planet_counter].calcNetForceExertedByX(planets);
	    	yForces[planet_counter] = planets[planet_counter].calcNetForceExertedByY(planets);
	  		planet_counter += 1;
	  	}
	  	planet_counter = 0;
    	while (planet_counter < planets.length) {
	  		planets[planet_counter].update(dt,xForces[planet_counter],yForces[planet_counter]);
	  		planet_counter +=1;
    	}
    	StdDraw.picture(0,0,"./images/starfield.jpg");
	  	planet_counter = 0;
	  	while(planet_counter<planets.length){
	  		planets[planet_counter].draw();
	  		planet_counter+=1;
	  	}
	  	StdDraw.show(10);
	  	timecounter+=dt;
	  }
    StdOut.printf("%d\n", planets.length);
    StdOut.printf("%.2e\n", universeradius);
    for (int i = 0; i < planets.length; i++) {
	     StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
   		  planets[i].xxPos, planets[i].yyPos, planets[i].xxVel, planets[i].yyVel, planets[i].mass, planets[i].imgFileName);
    }
  }
  public static double readRadius(String txt){
    In in = new In(txt);
    int numberofplanets = in.readInt();
    double radius = in.readDouble();
    return radius;
  }
  public static Planet[] readPlanets(String txt){
    In in = new In(txt);
    int numberofplanets = in.readInt();
    double radius = in.readDouble();
    int counter = 0;
    Planet[] y = new Planet[numberofplanets];
    while(counter<numberofplanets){
      double xxPos = in.readDouble();
      double yyPos = in.readDouble();
      double xxVel = in.readDouble();
      double yyVel = in.readDouble();
      double mass = in.readDouble();
      String imgFileName = in.readString();
      Planet s = new Planet(xxPos, yyPos, xxVel, yyVel, mass, imgFileName);
      y[counter] = s;
      counter+=1;
    }
    return y;
  }
}
