(function (global) {
  var fso=null, shell=null, sessionDir="", snapshotPath="", clientPath="", heartbeatPath="", statusPath="", stopPath="";
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
      "public delegate bool EnumProc(IntPtr h,IntPtr p);[StructLayout(LayoutKind.Sequential)]public struct RECT{public int Left,Top,Right,Bottom;}"+
      "[DllImport(\"user32.dll\")]public static extern bool EnumWindows(EnumProc c,IntPtr p);"+
      "[DllImport(\"user32.dll\")]public static extern bool IsWindowVisible(IntPtr h);"+
      "[DllImport(\"user32.dll\",CharSet=CharSet.Unicode)]public static extern int GetWindowText(IntPtr h,StringBuilder s,int n);"+
      "[DllImport(\"user32.dll\")]public static extern bool GetWindowRect(IntPtr h,out RECT r);"+
      "[DllImport(\"user32.dll\",CharSet=CharSet.Unicode)]public static extern IntPtr FindWindow(string c,string t);"+
      "[DllImport(\"user32.dll\",SetLastError=true)]public static extern int GetWindowLong(IntPtr h,int n);"+
      "[DllImport(\"user32.dll\",SetLastError=true)]public static extern int SetWindowLong(IntPtr h,int n,int v);"+
      "[DllImport(\"user32.dll\",SetLastError=true)]public static extern bool SetLayeredWindowAttributes(IntPtr h,uint k,byte a,uint f);"+
      "[DllImport(\"kernel32.dll\",CharSet=CharSet.Unicode,SetLastError=true)]public static extern bool MoveFileEx(string a,string b,int f);"+
      "public static string Snapshot(){var o=new StringBuilder();EnumProc cb=delegate(IntPtr h,IntPtr p){RECT r;var t=new StringBuilder(512);bool v=IsWindowVisible(h);"+
      "if(v&&GetWindowText(h,t,t.Capacity)>0&&GetWindowRect(h,out r)){string n=t.ToString().Replace('\\t',' ').Replace('\\r',' ').Replace('\\n',' ');"+
      "o.Append(h.ToInt64()).Append('\\t').Append(n).Append('\\t').Append(r.Left).Append('\\t').Append(r.Top).Append('\\t').Append(r.Right).Append('\\t').Append(r.Bottom).Append(\"\\t1\\r\\n\");}return true;};EnumWindows(cb,IntPtr.Zero);return o.ToString();}"+
      "public static long Configure(string title){IntPtr found=IntPtr.Zero;EnumProc cb=delegate(IntPtr h,IntPtr p){var t=new StringBuilder(512);GetWindowText(h,t,t.Capacity);if(t.ToString()==title){found=h;return false;}return true;};EnumWindows(cb,IntPtr.Zero);if(found==IntPtr.Zero)return 0;IntPtr h=found;int x=GetWindowLong(h,-20);SetWindowLong(h,-20,x|0x80000|0x80);"+
      "if(!SetLayeredWindowAttributes(h,0x00FF00FF,255,1))return -1;return h.ToInt64();}}";
    return "$ErrorActionPreference='Stop';$terrain="+(terrainEnabled?"$true":"$false")+";$snapshot="+q(snapshotPath)+";$heartbeat="+q(heartbeatPath)+";$client="+q(clientPath)+";$status="+q(statusPath)+";$stop="+q(stopPath)+";"+
      "try{Add-Type -TypeDefinition "+q(nativeCode)+";$hwnd=[DesktopPetNative]::Configure("+q(title)+");[IO.File]::WriteAllText($status,'running'+[char]9+$hwnd);"+
      "while($true){if(Test-Path -LiteralPath $stop){break};if(!(Test-Path -LiteralPath $client)){break};"+
      "$age=((Get-Date)-(Get-Item -LiteralPath $client).LastWriteTime).TotalSeconds;if($age -gt 15){break};"+
      "if($terrain){$tmp=$snapshot+'.tmp';[IO.File]::WriteAllText($tmp,[DesktopPetNative]::Snapshot(),[Text.Encoding]::UTF8);"+
      "if(![DesktopPetNative]::MoveFileEx($tmp,$snapshot,3)){throw 'Atomic snapshot replacement failed'}};"+
      "[IO.File]::WriteAllText($heartbeat,[DateTimeOffset]::Now.ToUnixTimeMilliseconds().ToString());Start-Sleep -Milliseconds 400};"+
      "[IO.File]::WriteAllText($status,'stopped')}catch{[IO.File]::WriteAllText($status,'error'+[char]9+$_.Exception.Message)}";
  }
  function start(title,enabled){
    try{fso=new ActiveXObject("Scripting.FileSystemObject");shell=new ActiveXObject("WScript.Shell");sessionId=(new Date()).getTime()+"_"+Math.floor(Math.random()*1000000);
      sessionDir=appFolder()+"\\data\\session_"+sessionId;fso.CreateFolder(sessionDir);snapshotPath=sessionDir+"\\windows.tsv";
      clientPath=sessionDir+"\\client.heartbeat";heartbeatPath=sessionDir+"\\bridge.heartbeat";statusPath=sessionDir+"\\status.txt";stopPath=sessionDir+"\\stop.request";
      write(clientPath,String((new Date()).getTime()));status="starting";
      var exe=shell.ExpandEnvironmentStrings("%SystemRoot%\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
      try{shell.Exec("\""+exe+"\" -NoLogo -NoProfile -NonInteractive -EncodedCommand "+base64Utf16(source(title,enabled)));}catch(launchResultError){}
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
