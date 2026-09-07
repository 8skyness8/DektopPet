(function (global) {
  var FALLING="FALLING", STANDING="STANDING", WALKING="WALKING";
  function create(x,y,w,h,collisionLeft,collisionRight,footOffsetY) { return {x:x,y:y,width:w,height:h,
    collisionLeft:collisionLeft,collisionRight:collisionRight,footOffsetY:footOffsetY,vy:0,direction:1,state:FALLING,
    supportHwnd:null,supportTitle:"",supportLeft:0,supportTop:0,dragging:false,standTicks:0}; }
  function fall(p) { p.state=FALLING;p.supportHwnd=null;p.supportTitle="";p.vy=0;p.standTicks=0; }
  function feetY(p) { return p.y+p.footOffsetY; }
  function overlaps(p,s) { return p.x+p.collisionRight>s.left+2 && p.x+p.collisionLeft<s.right-2; }
  function updateSupport(p,surfaces) {
    if(!p.supportHwnd)return;
    var s=TerrainModel.byHwnd(surfaces,p.supportHwnd);
    if(!s){fall(p);return;}
    p.x+=s.left-p.supportLeft;p.y=s.top-p.footOffsetY;p.supportLeft=s.left;p.supportTop=s.top;p.supportTitle=s.title;
    if(!overlaps(p,s))fall(p);
  }
  function step(p,surfaces,dt,bounds,walking) {
    if(p.dragging)return p;
    if(p.state!==FALLING)updateSupport(p,surfaces);
    var oldFeet=feetY(p);
    if(p.state===FALLING) {
      p.vy=Math.min(900,p.vy+1100*dt);p.y+=p.vy*dt;
      var newFeet=feetY(p),best=null;
      for(var i=0;i<surfaces.length;i++) if(overlaps(p,surfaces[i]) && oldFeet<=surfaces[i].top+2 && newFeet>=surfaces[i].top) {
        if(best===null || surfaces[i].top<best.top)best=surfaces[i];
      }
      if(best){p.y=best.top-p.footOffsetY;p.vy=0;p.state=walking?WALKING:STANDING;p.supportHwnd=best.hwnd;p.supportTitle=best.title;
        p.supportLeft=best.left;p.supportTop=best.top;}
      else if(feetY(p)>=bounds.bottom){p.y=bounds.bottom-p.footOffsetY;p.vy=0;p.state=walking?WALKING:STANDING;p.supportHwnd=null;p.supportTitle="";}
    } else {
      p.standTicks++;
      if(walking) {p.state=WALKING;p.x+=p.direction*42*dt;} else p.state=STANDING;
    }
    if(p.x+p.collisionLeft<bounds.left){p.x=bounds.left-p.collisionLeft;p.direction=1;} if(p.x+p.collisionRight>bounds.right){p.x=bounds.right-p.collisionRight;p.direction=-1;}
    if(p.supportHwnd){var support=TerrainModel.byHwnd(surfaces,p.supportHwnd);if(support&&!overlaps(p,support))fall(p);}
    return p;
  }
  global.PetPhysics={FALLING:FALLING,STANDING:STANDING,WALKING:WALKING,create:create,fall:fall,feetY:feetY,step:step};
}(this));
