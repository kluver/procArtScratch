Dim WinScriptHost
Set WinScriptHost = CreateObject("WScript.Shell")
WinScriptHost.Run Chr(34) & "C:\Users\danie\IdeaProjects\procArtScratch\build\distributions\procArtScratch-1.0-SNAPSHOT\procArtScratch-1.0-SNAPSHOT\bin\procArtScratch.bat" & Chr(34), 0
Set WinScriptHost = Nothing