var DesktopPet=(function () {
  // CSS geometry: body spans stage x=10+9 through its 74px border box; feet end at y=24+19+3+64+13+3.
  var WIDTH=112,HEIGHT=128,COLLISION_LEFT=19,COLLISION_RIGHT=93,FOOT_OFFSET_Y=126;
  var settings={terrainBridge:true,walking:true,speech:false,debug:false};
  var state=null,surfaces=[],timer=null,lastTick=0,lastTerrain=0,bridgeInfo={status:"disabled",heartbeatAge:-1,hwnd:""};
  var fso=null,settingsPath="",dragDX=0,dragDY=0,stopping=false;
  function folder(){var p=decodeURI(location.pathname);if(p.charAt(0)==="/"&&p.charAt(2)===":")p=p.substr(1);p=p.replace(/\//g,"\\");return fso.GetParentFolderName(p);}
  function bool(v,fallback){v=String(v).toLowerCase();return v==="true"?true:(v==="false"?false:fallback);}
  function loadSettings(){
    try{settingsPath=folder()+"\\data\\settings.ini";if(!fso.FileExists(settingsPath))return;var h=fso.OpenTextFile(settingsPath,1,false,0),lines=h.ReadAll().replace(/\r/g,"").split("\n");h.Close();
      for(var i=0;i<lines.length;i++){var at=lines[i].indexOf("=");if(at<1)continue;var k=lines[i].substr(0,at),v=lines[i].substr(at+1);if(settings[k]!==undefined)settings[k]=bool(v,settings[k]);}
    }catch(e){}
  }
  function saveSettings(){try{var h=fso.CreateTextFile(settingsPath,true,false);h.Write("terrainBridge="+settings.terrainBridge+"\r\nwalking="+settings.walking+"\r\nspeech="+settings.speech+"\r\ndebug="+settings.debug+"\r\n");h.Close();}catch(e){}}
  function screenBounds(){return {left:0,top:0,right:screen.availWidth,bottom:screen.availHeight};}
  function start(){
    fso=new ActiveXObject("Scripting.FileSystemObject");loadSettings();
    var title="DesktopPet Internal "+(new Date()).getTime()+"_"+Math.floor(Math.random()*100000);document.title=title;
    window.resizeTo(WIDTH,HEIGHT);var x=Math.max(0,Math.floor((screen.availWidth-WIDTH)*0.65)),y=Math.max(0,Math.floor(screen.availHeight*0.12));window.moveTo(x,y);
    state=PetPhysics.create(x,y,WIDTH,HEIGHT,COLLISION_LEFT,COLLISION_RIGHT,FOOT_OFFSET_Y);document.onkeydown=keyDown;window.onblur=endDrag;window.onbeforeunload=stop;
    document.getElementById("debug").style.display=settings.debug?"block":"none";
    TerrainBridge.start(title,settings.terrainBridge);lastTick=(new Date()).getTime();timer=window.setInterval(tick,33);
  }
  function tick(){
    var now=(new Date()).getTime(),dt=Math.min(0.1,(now-lastTick)/1000);lastTick=now,bounds=screenBounds();
    if(now-lastTerrain>=400){bridgeInfo=TerrainBridge.poll();surfaces=TerrainModel.parse(bridgeInfo.text,bridgeInfo.hwnd,COLLISION_RIGHT-COLLISION_LEFT,bounds);lastTerrain=now;}
    if(!state.dragging){PetPhysics.step(state,surfaces,dt,bounds,settings.walking);window.moveTo(Math.round(state.x),Math.round(state.y));}
    renderDebug();
  }
  function shortTitle(value){value=String(value||"");return value.length>24?value.substr(0,21)+"...":value;}
  function renderDebug(){if(!settings.debug)return;var supported=state.supportHwnd!==null;document.getElementById("debug").innerText="state "+state.state+"\r\nx/y "+Math.round(state.x)+" / "+Math.round(state.y)+"\r\nsupport "+(state.supportHwnd||"screen")+(supported?"\r\nsupportTop "+Math.round(state.supportTop)+"\r\nfeetY "+Math.round(PetPhysics.feetY(state))+"\r\ntitle "+shortTitle(state.supportTitle):"")+"\r\nterrain "+surfaces.length+"\r\nheartbeat "+(bridgeInfo.heartbeatAge<0?"n/a":bridgeInfo.heartbeatAge+" ms")+"\r\nbridge "+bridgeInfo.status;}
  function beginDrag(){
    state.dragging=true;dragDX=event.screenX-window.screenLeft;dragDY=event.screenY-window.screenTop;var pet=document.getElementById("mascot");
    if(typeof pet.setCapture!=="undefined")try{pet.setCapture();}catch(e){}event.cancelBubble=true;return false;
  }
  function dragMove(){if(!state||!state.dragging)return;if(event.button===0){endDrag();return;}state.x=event.screenX-dragDX;state.y=event.screenY-dragDY;window.moveTo(Math.round(state.x),Math.round(state.y));}
  function endDrag(){if(!state||!state.dragging)return;state.dragging=false;var pet=document.getElementById("mascot");if(typeof pet.releaseCapture!=="undefined")try{pet.releaseCapture();}catch(e){}PetPhysics.fall(state);}
  function keyDown(){if(event.keyCode===27)endDrag();}
  function toggleDebug(){settings.debug=!settings.debug;document.getElementById("debug").style.display=settings.debug?"block":"none";saveSettings();}
  function stop(){if(stopping)return;stopping=true;if(timer!==null){window.clearInterval(timer);timer=null;}TerrainBridge.stop();saveSettings();}
  function exit(){stop();window.close();}
  return {start:start,stop:stop,exit:exit,beginDrag:beginDrag,dragMove:dragMove,endDrag:endDrag,toggleDebug:toggleDebug};
}());
