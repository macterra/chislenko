' this script is loosely based on the original script from Microsoft.
' Various flags and such for god-knows what...
Dim A_
	A_=False
Dim B_,C_,D_
	B_=False
	C_=False
	D_=False
Dim E_,F_,G_
	E_=False
	F_=False
	G_=3000			' Ooohhh! I know what this is! This is the amount of milliseconds for refresh
Dim H_(),I_		' H_() is an object array of users. This is also the cache
	I_=0
' Variables for different links to different images representing state in Messenger... I think
Dim J_,K_,L_,M_,N_,O_,P_,OffL
	J_="<OBJECT classid="""&"clsid:FB7199AB-79BF-11d2-8D94-0000F875C541"""&" codeType=application/x-oleobject id=MsgrApp width=0 height=0></OBJECT>"
	K_="<font face=verdana size=1>"
	M_="<img align=absbottom width=16 height=17 border=0 src="
	L_="<a href=""vbscript:op(-1)"" style=background-color:#fafad2;color:#000000>"&"Sign in to MSN Messenger Service"&"</a>"
	N_=M_&"images/online1.gif"&" ALT="""&"Online"&""">"
	O_=M_&"images/busy1.gif"&" ALT="""&"Busy"&""">"
	P_=M_&"images/idle1.gif"&" ALT="""&"Away"&""">"
	'Added by JH
	OffL = M_&"images/offline1.gif"&" ALT="""&"Offline"&""">"
	M_="<img align=absbottom width=16 height=17 border=0 src="
Dim Q_
	Q_=False
Dim ttl
	ttl=0
' added arrays for online and offline contacts
	Dim OnA()		' online contacts
	Dim OffA()		' offline contacts
' counters for amount of online and offline
	Dim OnCtr
		OnCtr=0
	Dim OffCtr
		OffCtr=0
' Online/Offline?
	
Sub DrawInitialState
	On Error Resume Next
	Dim R_
	R_=MsgrObj.LocalState
	If Err Then
		A_=False
	Else
		A_=True
	End If
	Err.Clear
	If A_=True Then
		document.all.getmsgr.style.display="none"
		DrawContacts
	Else
		document.all.getmsgr.style.display="block"
	End If
End Sub

Function HasMsgrApp()
	appload.innerHTML = J_
	On Error Resume Next
	Dim R_
	Set R_=MsgrApp
	If Err.description="" Then
		HasMsgrApp=True
	Else
		HasMsgrApp=False
	End If
	Err.Clear
End Function

Sub RefreshMC()
	If A_ Then
		If C_ Then
			D_=True
		Else
			D_=False
			DrawContacts
			SetRefreshTimer
			'msnfanatic
		End If
	End If
End Sub

Sub SetRefreshTimer()
	If Not C_ Then
		C_=True
		setTimeout "DoRefresh",G_,"VBScript"
	End If
End Sub

Sub DoRefresh()
	C_=False
	If D_ Then
		RefreshMC
	End If
End Sub

Sub DrawContacts
'********************************************************************
' Modified by JH
' DrawContacts: 
'	
On Error Resume Next
' new list of contacts to iterate thru
' ctr for list loop
	Dim i
		i = 0
' strings for output
	Dim z, zz
		z=""
		zz=""
' for div visibility
	Dim mU,mO,msgL,noneL,notOn,onli
		mU="none"
		mO="none"
		msgL="none"
		noneL="none"
		notOn="block"
		onli="block"
	If E_ Then
		mcClearCache
	End If
' The heart of the matter
	If MsgrObj.LocalState And 2 Then
	'Online
		If Not F_ Then
			mcLoadCache
		End If
		If I_>0 Then
			For i = 0 To ttl
				select case H_(i).State
					case 1 
						OffCtr = OffCtr + 1
					case else
						OnCtr = OnCtr + 1
				end select
			Next
			ReDim OnA(OnCtr)
			ReDim OffA(OffCtr)
			OnCtr = 0
			OffCtr = 0
		' loop to get FriendlyNames of contacts and put them in their respective arrays
			For i = 0 To ttl
				If H_(i).State=1 then
					Set OffA(OffCtr)=H_(i)
					OffCtr = OffCtr + 1
				Else
			 		Set OnA(OnCtr) = H_(i)
					OnCtr = OnCtr + 1
				End If
			Next
			' sort online users
			SortUsers2 0,OnCtr-1,True
			For i = 0 to OnCtr-1
					Dim onl
					Dim h
						onl=""
						h=""
					h = " href='VBScript:op(" & i & ")'"
					onl = fixName(OnA(i).FriendlyName,17)
					z = z & "<a" & h & " style=Background-color:#fafad2>" & getStateImage(OnA(i).State) & "</a> " & "<a " & h & " style=Background-color:#fafad2 title="""
					z = z & "Send an instant message to " & onl & "."
					z = z & """>" & K_ & onl
					z = z & "</font></a><br>"
			Next
			' sort offline users
			SortUsers2 0,OffCtr-1,False
			For i = 0 to OffCtr-1
					Dim ofn
						ofn=""	
					ofn = fixName(OffA(i).FriendlyName,17)
					zz = zz & getStateImage(OffA(i).State) & " "
					zz = zz & K_ & ofn & "</font><br>"
			Next
			if OnCtr > 0 Then
				mU="block"
				mO="block"
				document.all.mUser.innerHTML=z
				document.all.mOff.innerHTML=zz
			else
				mU="block"
				mO="block"
				document.all.mUser.innerHTML="<font face=verdana,sans-serif size=1 style=color:#000000>None</font>"
				document.all.mOff.innerHTML=zz
			end if
		Else
			noneL="block"
			document.all.noneol.innerHTML=K_&"Your contact list is empty. <br><a href=vbscript:op(-2) style=color:#000000>Add contacts to your list.</a>"&"</font>"		
		end if
	Else
		If MsgrObj.LocalState=256 Or MsgrObj.LocalState=512 Then
			msgL="block"
			notOn="none"
			onli="none"
			B_ = True
			document.all.status.innerHTML = "Signing in..."
		Else
			msgL="block"
			notOn="none"
			onli="none"
			if Not B_ Then
				document.all.status.innerHTML = L_
			End If
		End If
	End If		
	document.all.Online.style.display=onli
	document.all.mUser.style.display=mU
	document.all.notOnline.style.display=notOn
	document.all.mOff.style.display=mO
	document.all.msgrlogon.style.display=msgL
	document.all.noneol.style.display=noneL

End Sub
'<!--MSN FAN-->
Sub mcClearCache
	I_=0
	Erase H_
	Erase OnA
	Erase OffA
	F_=False
	E_=False
	D_=True
End Sub

Sub mcLoadCache
	Dim BB_

	Set BB_=MsgrObj.List(0)

	Dim CB_
		CB_=0
	Dim DB_
		DB_=BB_.Count
		ttl=DB_ -1
	Redim H_(DB_)
	For Each u In BB_
		Set H_(CB_)=u
		CB_=CB_+1
	Next
	I_=CB_
	SortUsers 0,I_-1
	F_=True
End Sub

' Added by JH
' Sorts Online/Offline users
Sub SortUsers2(EB_,FB_,IsOn)
	Dim GB_
	if(IsOn) then
		if FB_>EB_ then
			GB_=ptnOn(EB_,FB_)
			SortUsers2 EB_,GB_-1,True
			SortUsers2 GB_+1,FB_,True
		end if
	else
		if FB_>EB_ then
			GB_=ptnOff(EB_,FB_)
			SortUsers2 EB_,GB_-1,False
			SortUsers2 GB_+1,FB_,False
		end if
	end if		
End Sub

Sub SortUsers(EB_,FB_)
	Dim GB_
	if FB_>EB_ then
		GB_=ptn(EB_,FB_)
		SortUsers EB_,GB_-1
		SortUsers GB_+1,FB_
	end if
End Sub

' Added by JH
' 
Function ptnOn(EB_,FB_)
	Dim HB_,tmp
	Randomize
	HB_=Int(Rnd()Mod(FB_-EB_+1))+EB_
	Set tmp=OnA(HB_)
	Set OnA(HB_)=OnA(EB_)
	Set OnA(EB_)=tmp
	Dim a,b
	a=EB_
	b=FB_
	While b>a
		If StrComp(OnA(b).FriendlyName,tmp.FriendlyName,1)>=0 Then
			b=b-1
		Else
			Set OnA(a)=OnA(b)
			Set OnA(b)=OnA(a+1)
			Set OnA(a+1)=tmp
			a=a+1
		End If
	Wend
	ptnOn=a
End Function

' Added by JH
'
Function ptnOff(EB_,FB_)
	Dim HB_,tmp
	Randomize
	HB_=Int(Rnd()Mod(FB_-EB_+1))+EB_
	Set tmp=OffA(HB_)
	Set OffA(HB_)=OffA(EB_)
	Set OffA(EB_)=tmp
	Dim a,b
	a=EB_
	b=FB_
	While b>a
		If StrComp(OffA(b).FriendlyName,tmp.FriendlyName,1)>=0 Then
			b=b-1
		Else
			Set OffA(a)=OffA(b)
			Set OffA(b)=OffA(a+1)
			Set OffA(a+1)=tmp
			a=a+1
		End If
	Wend
	ptnOff=a
End Function

SUB MsgrObj_OnLocalStateChangeResult(ByVal hr,ByVal mLocalState,pService)
	If 0=hr And Err.description="" And A_ Then
		If mLocalState=256 Or mLocalState=512 Then
			B_=True
			document.all.status.innerHTML="Signing in..."
		ElseIf mLocalState=1024 Then
			B_=True
			document.all.status.innerHTML="Signing out..."
		ElseIf mLocalState=1 then
			B_=True
			document.all.status.innerHTML=L_
		End If
		RefreshMC
	End If
END SUB

SUB MsgrObj_OnUserStateChanged(pUser,ByVal mPrevState,pfEnableDefault)
	'If Err.description="" Then
		mcClearCache
		B_=False
		RefreshMC
	'End If
END SUB

SUB MsgrObj_OnListRemoveResult(ByVal hr,ByVal MLIST,ByVal pUser)
	If 0=hr And 0=MLIST And Err.description="" Then
		E_=True
		RefreshMC
	End If
END SUB

SUB MsgrObj_OnListAddResult(ByVal hr,ByVal MLIST,ByVal pUser)
	If 0=hr And 0=MLIST And Err.description="" Then
		E_=True
		RefreshMC
	End If
END SUB

SUB MsgrObj_OnLogonResult(ByVal hr,ByVal pService)
	If 0=hr And Err.description="" Then
		mcClearCache
		B_=False
		RefreshMC
	Else
		mcClearCache
		B_=False
		RefreshMC
	End If
END SUB

SUB MsgrObj_OnLogoff()
	mcClearCache
	B_=False
	RefreshMC
END SUB

SUB MsgrObj_OnAppShutdown()
	RefreshMC
END SUB


' Launches chat window for a given user, or
' launches the logon window, or simply brings up
' Messenger to show all contacts.

Function op(n)
	If HasMsgrApp Then
		If n>=0 Then
			document.all.mctrack.src="P/6/"
			On Error Resume Next
			MsgrApp.LaunchIMUI OnA(n)
		ElseIf-1=n Then
			MsgrApp.LaunchLogonUI
		Else
			MsgrApp.Visible=1
		End If
	End If
End Function


Function htmlesc(str)
	str=Replace(str,"&","&amp;")
	str=Replace(str,"<","&lt;")
	htmlesc=Replace(str,">","&gt;")
End Function

Function fixName(s,max)
	If Len(s)>max Then
		s=Left(s,max-2)&"..."
	End If
		fixName=htmlesc(s)
End Function

Function getStateImage(t)
	Select Case t
	Case 1
		getStateImage=OffL	'Offline
	Case 2
		getStateImage=N_	'Online
	Case 10
		getStateImage=O_	'Busy
	Case 14
		getStateImage=P_	'BRB
	Case 18
		getStateImage=P_	'Away
	Case 34
		getStateImage=P_	'Away... as well... hmmmmmmmmm....
	Case 50
		getStateImage=O_	'On The Phone
	Case 66
		getStateImage=O_	'Out To Lunch
	End Select
End Function