
// Worm.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Contains the worm's internal data structure (a circular buffer)
   and code for deciding on the position and compass direction
   of the next worm move.
*/

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;


public class Worm
{
  // size and number of dots in a worm
  private static final int DOTSIZE = 12;
  private static final int RADIUS = DOTSIZE/2;
  private static final int MAXPOINTS = 40;

  // compass direction/bearing constants
  private static final int NUM_DIRS = 8;
  private static final int N = 0;  // north, etc going clockwise
  private static final int NE = 1;
  private static final int E = 2;
  private static final int SE = 3;
  private static final int S = 4;
  private static final int SW = 5;
  private static final int W = 6;
  private static final int NW = 7;

  private int currCompass;  // stores the current compass dir/bearing

  // Stores the increments in each of the compass dirs.
  // An increment is added to the old head position to get the
  // new position.
  Point2D.Double incrs[];

  // Probability info for selecting a compass dir.
  private static final int NUM_PROBS = 9;
  private int probsForOffset[];

  // cells[] stores the dots making up the worm
  // it is treated like a circular buffer
  private Point2D cells[];
  private int nPoints;
  private int tailPosn, headPosn;   // the tail and head of the buffer

  private int pWidth, pHeight;   // panel dimensions
  private Obstacles obs;


  public Worm(int pW, int pH, Obstacles os)
  {
    pWidth = pW; pHeight = pH;
    obs = os;
    cells = new Point2D[MAXPOINTS];   // initialise buffer
    nPoints = 0;
    headPosn = -1;  tailPosn = -1;

    // increments for each compass dir
    incrs = new Point2D.Double[NUM_DIRS];
    incrs[N] = new Point2D.Double(0.0, -1.0);
    incrs[NE] = new Point2D.Double(0.7, -0.7);
    incrs[E] = new Point2D.Double(1.0, 0.0);
    incrs[SE] = new Point2D.Double(0.7, 0.7);
    incrs[S] = new Point2D.Double(0.0, 1.0);
    incrs[SW] = new Point2D.Double(-0.7, 0.7);
    incrs[W] = new Point2D.Double(-1.0, 0.0);
    incrs[NW] = new Point2D.Double(-0.7, -0.7);

    // probability info for selecting a compass dir.
    //    0 = no change, -1 means 1 step anti-clockwise,
    //    1 means 1 step clockwise, etc.
    /* The array means that usually the worm continues in
       the same direction but may bear slightly to the left
       or right. */
    probsForOffset = new int[NUM_PROBS];
    probsForOffset[0] = 0;  probsForOffset[1] = 0;
    probsForOffset[2] = 0;  probsForOffset[3] = 1;
    probsForOffset[4] = 1;  probsForOffset[5] = 2;
    probsForOffset[6] = -1;  probsForOffset[7] = -1;
    probsForOffset[8] = -2;

  } // end of Worm()


  public boolean nearHead(int x, int y)
  // is (x,y) near the worm's head?
  { if (nPoints > 0) {
      if( (Math.abs( cells[headPosn].getX() + RADIUS - x) <= DOTSIZE) &&
           (Math.abs( cells[headPosn].getY() + RADIUS - y) <= DOTSIZE) )
        return true;
    }
    return false;
  } // end of nearHead()


  public boolean touchedAt(int x, int y)
  // is (x,y) near any part of the worm's body?
  {
    int i = tailPosn;
    while (i != headPosn) {
      if( (Math.abs( cells[i].getX() + RADIUS - x) <= RADIUS) &&
          (Math.abs( cells[i].getY() + RADIUS - y) <= RADIUS) )
        return true;
      i = (i+1) % MAXPOINTS;
    }
    return false;
  }  // end of touchedAt()


  public void move()
  /* A move causes the addition of a new dot to the front of
     the worm, which becomes its new head. A dot has a position
     and compass direction/bearing, which is derived from the
     position and bearing of the old head.

     move() is complicated by having to deal with 3 cases:
       * when the worm is first created
       * when the worm is growing
       * when the worm is MAXPOINTS long (then the addition
         of a new head must be balanced by the removal of a
         tail dot)
  */
  {
    int prevPosn = headPosn;  // save old head posn while creating new one
    headPosn = (headPosn + 1) % MAXPOINTS;

    if (nPoints == 0) {   // empty array at start
      tailPosn = headPosn;
      currCompass = (int)( Math.random()*NUM_DIRS );  // random dir.
      cells[headPosn] = new Point2D.Double( pWidth/2, pHeight/2 ); // center pt
      nPoints++;
    }
    else if (nPoints == MAXPOINTS) {    // array is full
      tailPosn = (tailPosn + 1) % MAXPOINTS;    // forget last tail
      newHead(prevPosn);
    }
    else {     // still room in cells[]
      newHead(prevPosn);
      nPoints++;
    }
  }  // end of move()


  private void newHead(int prevPosn)
  /* Create new head position and compass direction/bearing.

     This has two main parts. Initially we try to generate
     a head by varying the old position/bearing. But if
     the new head hits an obstacle, then we shift
     to a second phase. 

     In the second phase we try a head which is 90 degrees
     clockwise, 90 degress clockwise, or 180 degrees reversed
     so that the obstacle is avoided. These bearings are 
     stored in fixedOffs[].
  */
  {
    Point2D newPt;
    int newBearing;
    int fixedOffs[] = {-2, 2, -4};  // offsets to avoid an obstacle

    newBearing = varyBearing();
    newPt = nextPoint(prevPosn, newBearing );
      // Get a new position based on a semi-random
      // variation of the current position.

    if (obs.hits(newPt, DOTSIZE)) {
      for (int i=0; i < fixedOffs.length; i++) {
        newBearing = calcBearing(fixedOffs[i]);
        newPt = nextPoint(prevPosn, newBearing);
        if (!obs.hits(newPt, DOTSIZE))
          break;     // one of the fixed offsets will work
      }
    }
    cells[headPosn] = newPt;     // new head position
    currCompass = newBearing;    // new compass direction
  }  // end of newHead()


  private int varyBearing()
  // vary the compass bearing semi-randomly 
  {
    int newOffset = probsForOffset[ (int)( Math.random()*NUM_PROBS )];
    return calcBearing( newOffset );
  }  // end of varyBearing()


  private int calcBearing(int offset)
  // Use the offset to calculate a new compass bearing based
  // on the current compass direction.
  {
    int turn = currCompass + offset;
    // ensure that turn is between N to NW (0 to 7)
    if (turn >= NUM_DIRS)
      turn = turn - NUM_DIRS;
    else if (turn < 0)
      turn = NUM_DIRS + turn;
    return turn;
  }  // end of calcBearing()



  private Point2D nextPoint(int prevPosn, int bearing)
  /* Return the next coordinate based on the previous position
     and a compass bearing.

     Convert the compass bearing into predetermined increments 
     (stored in incrs[]). Add the increments multiplied by the 
     DOTSIZE to the old head position.
     Deal with wraparound.
  */
  { 
    // get the increments for the compass bearing
    Point2D.Double incr = incrs[bearing];

    int newX = (int) (cells[prevPosn].getX() + DOTSIZE * incr.getX());
    int newY = (int) (cells[prevPosn].getY() + DOTSIZE * incr.getY());

    // modify newX/newY if < 0, or > pWidth/pHeight; use wraparound 
    if (newX+DOTSIZE < 0)     // is right hand edge invisible?
      newX = newX + pWidth;
    else  if (newX > pWidth)
      newX = newX - pWidth;

    if (newY+DOTSIZE < 0)     // is bottom edge invisible?
      newY = newY + pHeight;
    else  if (newY > pHeight)
      newY = newY - pHeight;

    return new Point2D.Double(newX,newY);
  }  // end of nextPoint()


  public void draw(Graphics g)
  // draw a black worm with a red head
  {
    if (nPoints > 0) {
      g.setColor(Color.black);
      int i = tailPosn;
      while (i != headPosn) {
        g.fillOval((int) cells[i].getX(), (int) cells[i].getY(), DOTSIZE, DOTSIZE);
        i = (i+1) % MAXPOINTS;
      }
      g.setColor(Color.red);
      g.fillOval( (int) cells[headPosn].getX(), (int) cells[headPosn].getY(), DOTSIZE, DOTSIZE);
    }
  }  // end of draw()

}  // end of Worm class

