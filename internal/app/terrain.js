(function (global) {
  function number(value) { var n=Number(value); return isFinite(n)?n:null; }
  function parse(text, ownHwnd, petWidth, bounds) {
    var rows=String(text||"").replace(/\r/g,"").split("\n"), surfaces=[], i, c, l, t, r, b;
    for(i=0;i<rows.length;i++) {
      c=rows[i].split("\t");
      if(c.length<7 || c[0]===String(ownHwnd) || c[1]==="Program Manager") continue;
      l=number(c[2]); t=number(c[3]); r=number(c[4]); b=number(c[5]);
      if(l===null || t===null || r===null || b===null || c[6]!=="1") continue;
      if(r<=l || b<=t || t<=-30000 || l<=-30000 || r-l<petWidth || b-t<30) continue;
      // Batch A physics is intentionally single-monitor.  The PowerShell bridge enumerates
      // the whole virtual desktop, so reject windows that do not intersect the same screen
      // coordinate space the HTA physics currently uses (0..availWidth / 0..availHeight).
      if(bounds && (r<=bounds.left || l>=bounds.right || b<=bounds.top || t>=bounds.bottom)) continue;
      surfaces.push({hwnd:c[0],title:c[1],left:l,top:t,right:r,bottom:b});
    }
    return surfaces;
  }
  function byHwnd(surfaces, hwnd) {
    for(var i=0;i<surfaces.length;i++) if(surfaces[i].hwnd===String(hwnd)) return surfaces[i];
    return null;
  }
  global.TerrainModel={parse:parse,byHwnd:byHwnd};
}(this));
