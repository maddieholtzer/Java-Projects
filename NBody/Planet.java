public class Planet {
  public double xxPos;
  public double yyPos;
  public double xxVel;
  public double yyVel;
  public double mass;
  public String imgFileName;

  public Planet(double xP, double yP, double xV,
              double yV, double m, String img) {
    xxPos = xP;
    yyPos = yP;
    xxVel = xV;
    yyVel = yV;
    mass = m;
    imgFileName = img;
  }

  public Planet(Planet p) {
    this.xxPos = p.xxPos;
    this.yyPos = p.yyPos;
    this.xxVel = p.xxVel;
    this.yyVel = p.yyVel;
    this.mass = p.mass;
    this.imgFileName = p.imgFileName;
  }

  public double calcDistance(Planet p){
    double length = Math.abs(this.xxPos-p.xxPos);
    double height = Math.abs(this.yyPos-p.yyPos);
    double distance = (length*length) + (height*height);
    distance = Math.sqrt(distance);
    return distance;
  }

  public double calcForceExertedBy(Planet p){
    double g = 6.67 * Math.pow(10,-11);
    double force = ( g * p.mass * this.mass)/(calcDistance(p)*calcDistance(p));
    return force;
  }

  public double calcForceExertedByY(Planet p) {
      double r = calcDistance(p);
      double dy = p.yyPos-this.yyPos;
      double f = calcForceExertedBy(p);
      double forcex = f * dy / r;
      return forcex;
    }

    public double calcForceExertedByX(Planet p) {
      double r = calcDistance(p);
      double dx = p.xxPos-this.xxPos;
      double f = calcForceExertedBy(p);
      double forcex = f * dx / r;
      return forcex;
    }

    public double calcNetForceExertedByX(Planet[] x){
      double NetForceExertedByX = 0;
      int counter = 0;
      while(counter<x.length){
        if(this.equals(x[counter])){
          NetForceExertedByX = NetForceExertedByX + 0;
        }
        else{
          NetForceExertedByX=NetForceExertedByX+ calcForceExertedByX(x[counter]);
        }
        counter+=1;
      }
      return NetForceExertedByX;
    }

    public double calcNetForceExertedByY(Planet[] x){
      double NetForceExertedByY = 0;
      int counter = 0;
      while(counter<x.length){
        if(this.equals(x[counter])){
          NetForceExertedByY = NetForceExertedByY + 0;
        }
        else{
          NetForceExertedByY=NetForceExertedByY+ calcForceExertedByY(x[counter]);
        }
        counter+=1;
      }
      return NetForceExertedByY;
    }
    public void update(double dt, double fX, double fY) {
     double ax = fX / this.mass;
     double ay = fY / this.mass;
     double vx = this.xxVel + dt * ax;
     double vy = this.yyVel + dt * ay;
     this.xxPos = this.xxPos + dt*vx;
     this.yyPos = this.yyPos + dt*vy;
     this.xxVel = vx;
     this.yyVel = vy;
   }
   public void draw(){
     String imgpath = "./images/" + this.imgFileName;
     StdDraw.picture(this.xxPos,this.yyPos,imgpath);
   }
}

