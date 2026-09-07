(function (global) {
  var fso=null, shell=null, sessionDir="", snapshotPath="", clientPath="", heartbeatPath="", statusPath="", stopPath="", launchErrorPath="";
  var started=false, sessionId="", ownHwnd="", lastHeartbeat=0, status="disabled";
  function q(v){return "'"+String(v).replace(/'/g,"''")+"'";}
  function base64Utf16(value){
    var alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",bytes=[],out="",i,n;
    for(i=0;i<value.length;i++){n=value.charCodeAt(i);bytes.push(n&255,(n>>8)&255);}
    for(i=0;i<bytes.length;i+=3){n=(bytes[i]<<16)|((i+1<bytes.length?bytes[i+1]:0)<<8)|(i+2<bytes.length?bytes[i+2]:0);
      out+=alphabet.charAt((n>>18)&63)+alphabet.charAt((n>>12)&63)+(i+1<bytes.length?alphabet.charAt((n>>6)&63):"=")+(i+2<bytes.length?alphabet.charAt(n&63):"=");}
    return out;
  }
  function write(path,value){var h=fso.CreateTextFile(path,true,false);h.Write(String(value));h.Close();}
  function read(path){try{if(!fso.FileExists(path))return "";var h=fso.OpenTextFile(path,1,false,0),v=h.ReadAll();h.Close();return v;}catch(e){return "";}}
  function appFolder(){var p=decodeURI(location.pathname);if(p.charAt(0)==="/"&&p.charAt(2)===":")p=p.substr(1);p=p.replace(/\//g,"\\");return fso.GetParentFolderName(p);}
  function source(title,terrainEnabled){
    var nativeCode="using System;using System.Text;using System.Runtime.InteropServices;public static class DesktopPetNative{"+
      "public delegate bool EnumProc(IntPtr windowHandle,IntPtr parameter);[StructLayout(LayoutKind.Sequential)]public struct RECT{public int Left,Top,Right,Bottom;}"+
      "[DllImport(\"user32.dll\")]public static extern bool EnumWindows(EnumProc callback,IntPtr parameter);"+
      "[DllImport(\"user32.dll\")]public static extern bool IsWindowVisible(IntPtr windowHandle);"+
      "[DllImport(\"user32.dll\",CharSet=CharSet.Unicode)]public static extern int GetWindowText(IntPtr windowHandle,StringBuilder text,int capacity);"+
      "[DllImport(\"user32.dll\")]public static extern bool GetWindowRect(IntPtr windowHandle,out RECT rectangle);"+
      "[DllImport(\"user32.dll\",SetLastError=true)]public static extern int GetWindowLong(IntPtr windowHandle,int index);"+
      "[DllImport(\"user32.dll\",SetLastError=true)]public static extern int SetWindowLong(IntPtr windowHandle,int index,int value);"+
      "[DllImport(\"user32.dll\",SetLastError=true)]public static extern bool SetLayeredWindowAttributes(IntPtr windowHandle,uint colorKey,byte alpha,uint flags);"+
      "[DllImport(\"dwmapi.dll\")]public static extern int DwmGetWindowAttribute(IntPtr windowHandle,int attribute,out RECT rectangle,int rectangleSize);"+
      "[DllImport(\"kernel32.dll\",CharSet=CharSet.Unicode,SetLastError=true)]public static extern bool MoveFileEx(string existingPath,string replacementPath,int flags);"+
      "private static bool PlausibleFrame(RECT fallbackRectangle,RECT candidateRectangle){int candidateWidth=candidateRectangle.Right-candidateRectangle.Left;int candidateHeight=candidateRectangle.Bottom-candidateRectangle.Top;if(candidateWidth<=0||candidateHeight<=0||candidateRectangle.Left<=-30000||candidateRectangle.Top<=-30000)return false;"+
      "return Math.Abs((long)candidateRectangle.Left-fallbackRectangle.Left)<=64&&Math.Abs((long)candidateRectangle.Top-fallbackRectangle.Top)<=64&&Math.Abs((long)candidateRectangle.Right-fallbackRectangle.Right)<=64&&Math.Abs((long)candidateRectangle.Bottom-fallbackRectangle.Bottom)<=64;}"+
      "public static string Snapshot(){StringBuilder output=new StringBuilder();EnumProc snapshotCallback=delegate(IntPtr windowHandle,IntPtr parameter){RECT windowRectangle;StringBuilder titleBuilder=new StringBuilder(512);bool visible=IsWindowVisible(windowHandle);"+
      "if(visible&&GetWindowText(windowHandle,titleBuilder,titleBuilder.Capacity)>0&&GetWindowRect(windowHandle,out windowRectangle)){RECT terrainRectangle=windowRectangle;try{RECT frameRectangle;int dwmResult=DwmGetWindowAttribute(windowHandle,9,out frameRectangle,Marshal.SizeOf(typeof(RECT)));if(dwmResult==0&&PlausibleFrame(windowRectangle,frameRectangle))terrainRectangle=frameRectangle;}catch{}string windowTitle=titleBuilder.ToString().Replace('\\t',' ').Replace('\\r',' ').Replace('\\n',' ');"+
      "output.Append(windowHandle.ToInt64()).Append('\\t').Append(windowTitle).Append('\\t').Append(terrainRectangle.Left).Append('\\t').Append(terrainRectangle.Top).Append('\\t').Append(terrainRectangle.Right).Append('\\t').Append(terrainRectangle.Bottom).Append(\"\\t1\\r\\n\");}return true;};EnumWindows(snapshotCallback,IntPtr.Zero);return output.ToString();}"+
      "public static long Configure(string expectedTitle){IntPtr foundWindow=IntPtr.Zero;EnumProc findCallback=delegate(IntPtr windowHandle,IntPtr parameter){StringBuilder titleBuilder=new StringBuilder(512);GetWindowText(windowHandle,titleBuilder,titleBuilder.Capacity);if(titleBuilder.ToString()==expectedTitle){foundWindow=windowHandle;return false;}return true;};EnumWindows(findCallback,IntPtr.Zero);if(foundWindow==IntPtr.Zero)return 0;IntPtr targetWindow=foundWindow;int extendedStyle=GetWindowLong(targetWindow,-20);SetWindowLong(targetWindow,-20,extendedStyle|0x80000|0x80);"+
      "if(!SetLayeredWindowAttributes(targetWindow,0x00FF00FF,255,1))return -1;return targetWindow.ToInt64();}}";
    return "$ErrorActionPreference='Stop';$terrain="+(terrainEnabled?"$true":"$false")+";$snapshot="+q(snapshotPath)+";$heartbeat="+q(heartbeatPath)+";$client="+q(clientPath)+";$status="+q(statusPath)+";$stop="+q(stopPath)+";"+
      "[IO.File]::WriteAllText($status,'starting');try{Add-Type -TypeDefinition "+q(nativeCode)+"}catch{$message=$_.Exception.Message -replace '[\\r\\n\\t]+',' ';[IO.File]::WriteAllText($status,'error'+[char]9+'ADD_TYPE_COMPILE'+[char]9+$message);return};"+
      "[IO.File]::WriteAllText($status,'native_ready');try{$hwnd=[DesktopPetNative]::Configure("+q(title)+");[IO.File]::WriteAllText($status,'running'+[char]9+$hwnd);"+
      "while($true){if(Test-Path -LiteralPath $stop){break};if(!(Test-Path -LiteralPath $client)){break};"+
      "$age=((Get-Date)-(Get-Item -LiteralPath $client).LastWriteTime).TotalSeconds;if($age -gt 15){break};"+
      "if($terrain){$tmp=$snapshot+'.tmp';[IO.File]::WriteAllText($tmp,[DesktopPetNative]::Snapshot(),[Text.Encoding]::UTF8);"+
      "if(![DesktopPetNative]::MoveFileEx($tmp,$snapshot,3)){throw 'Atomic snapshot replacement failed'}};"+
      "[IO.File]::WriteAllText($heartbeat,[DateTimeOffset]::Now.ToUnixTimeMilliseconds().ToString());Start-Sleep -Milliseconds 400};"+
      "[IO.File]::WriteAllText($status,'stopped')}catch{$message=$_.Exception.Message -replace '[\\r\\n\\t]+',' ';[IO.File]::WriteAllText($status,'error'+[char]9+'BRIDGE_RUNTIME'+[char]9+$message)}";
  }
  function start(title,enabled){
    try{fso=new ActiveXObject("Scripting.FileSystemObject");shell=new ActiveXObject("WScript.Shell");sessionId=(new Date()).getTime()+"_"+Math.floor(Math.random()*1000000);
      sessionDir=appFolder()+"\\data\\session_"+sessionId;fso.CreateFolder(sessionDir);snapshotPath=sessionDir+"\\windows.tsv";
      clientPath=sessionDir+"\\client.heartbeat";heartbeatPath=sessionDir+"\\bridge.heartbeat";statusPath=sessionDir+"\\status.txt";stopPath=sessionDir+"\\stop.request";launchErrorPath=sessionDir+"\\launch_error.txt";
      write(clientPath,String((new Date()).getTime()));write(statusPath,"starting");status="starting";
      var exe=shell.ExpandEnvironmentStrings("%SystemRoot%\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
      try{shell.Exec("\""+exe+"\" -NoLogo -NoProfile -NonInteractive -EncodedCommand "+base64Utf16(source(title,enabled)));}catch(launchResultError){write(launchErrorPath,String(launchResultError.description||launchResultError.message));status="launch return error; awaiting file status";}
      started=true;
    }catch(e){status="error: "+e.description;started=false;}
  }
  function poll(){
    if(!started)return {text:"",hwnd:ownHwnd,status:status,heartbeatAge:-1};
    try{write(clientPath,String((new Date()).getTime()));var state=read(statusPath).replace(/\r|\n/g,"");if(state){status=state;var c=state.split("\t");if(c.length>1&&Number(c[1])>0)ownHwnd=c[1];}
      var hb=read(heartbeatPath);if(hb)lastHeartbeat=Number(hb);return {text:read(snapshotPath),hwnd:ownHwnd,status:status,heartbeatAge:lastHeartbeat?((new Date()).getTime()-lastHeartbeat):-1};
    }catch(e){status="error: "+e.description;return {text:"",hwnd:ownHwnd,status:status,heartbeatAge:-1};}
  }
  function stop(){if(!started)return;try{write(stopPath,"stop");}catch(e){}started=false;}
  global.TerrainBridge={start:start,poll:poll,stop:stop};
}(this));
