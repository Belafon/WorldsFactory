
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Configuration;
using Avalonia.Threading;
using Avalonia.Controls;
using WorldsFactory.screen;
using WorldsFactory.screen.panelCards;
using WorldsFactory.screen.panelCards.cards;
using WorldsFactory.project;
using Serilog;

namespace WorldsFactory.boot;

/// <summary>
/// Boot class is responsible for checking the version of the program and keeping it up to date, 
/// checking if the program runs for the first time, and setting up basic functions.
/// It also creates new Program object and opens the first main window.
/// </summary>
public class Boot
{
	/// <summary>
	/// Checks the version of the program and keeps it up to date, 
	/// Checks if the program runs for the first time, and sets up basic functions.
	/// It also creates new Program object and opens the first main window.
	/// </summary>
	public Boot(Window bootWindow)
	{
		string version = loadVersion();
		// TODO: checkUpdates(version);

		bool isFirstRun = loadIsFirstRun();

		// verifies, if the appData folder exists, if not, creates it
		verifyAppDataFolder();

		// TODO: setup basic functions

		Program program = new Program(version, isFirstRun);

		Thread.Sleep(500);

		Dispatcher.UIThread.Post(() => Windows.openFirstMainWindow(bootWindow, program), DispatcherPriority.Background);
		Thread.Sleep(10); // TODO
		Dispatcher.UIThread.Post(() =>
		{
			bootWindow.Close();
			PanelCardView panel = new PanelCardView(Windows.MainWindows[0]);
			//((PanelCardView)Windows.MainWindows[0].RootContentGrid).splitPanel(panel, Side.Left);
		}, DispatcherPriority.Background);
	}


	private void checkUpdates(string thisVersionInString)
	{
		string currentVersionInString = thisVersionInString;

		try
		{ 
			TcpClient client = new TcpClient("127.0.0.1", 18888);
			Byte[] data = System.Text.Encoding.ASCII.GetBytes("version");
			NetworkStream stream = client.GetStream();
			stream.Write(data, 0, data.Length);

			data = new Byte[256];
			String responseData = String.Empty;

			// Read the first batch of the TcpServer response bytes.
			Int32 bytes = stream.Read(data, 0, data.Length);
			currentVersionInString = System.Text.Encoding.ASCII.GetString(data, 0, bytes);

			stream.Close();
			client.Close();
		}
		catch (ArgumentNullException e)
		{
			Log.Warning("Boot: " + e);
		}
		catch (SocketException e)
		{
			Log.Warning("Boot: " + e);
		}
		catch (FormatException e)
		{
			Log.Warning("Boot: " + e);
		}
		catch (OverflowException e)
		{
			Log.Warning("Boot: " + e);
		}

		int[] currentVersion = convertVersionFromStringToIntArray(currentVersionInString);
		int[] thisVersion = convertVersionFromStringToIntArray(thisVersionInString);

		int sizeOfUpdate = isThisVersionOutdated(thisVersion, currentVersion);
		if (sizeOfUpdate > 0)
		{
			// TODO: update
			Log.Information("Boot: new update, from version " + thisVersionInString + " to version " + currentVersionInString);
			ConfigurationManager.AppSettings.Set("appVersion", currentVersionInString);
		}
	}

	private int[] convertVersionFromStringToIntArray(string version)
	{
		string[] versionArray = version.Split('.');
		int[] o = new int[versionArray.Length];
		try
		{
			for (int i = 0; i < versionArray.Length; i++)
				o[i] = Convert.ToInt32(versionArray[i]);
		}
		catch (System.Exception)
		{
			Log.Error("Boot: convertVersionFromStringToInt, version cannot be convert");
		}
		return o;
	}

	/// <summary>
	/// returns size of update, where 1 is the biggist one, 
	/// 2 and so on are smaller updates 
	/// 0 means no update
	/// </summary>
	private int isThisVersionOutdated(int[] thisVersion, int[] currentVersion)
	{
		for (int i = 0; i < currentVersion.Length; i++)
			if (currentVersion[i] > thisVersion[i])
				return i + 1;
		return 0;
	}

	string loadVersion()
	{
		string? v = ConfigurationManager.AppSettings.Get("appVersion");
		if (v is not null)
			return v;
		else
		{
			Log.Error("Boot: loadVersion() cannot load data from App.config");
			return "9999.9999";
		}
	}

	private bool loadIsFirstRun()
	{
		string? v = ConfigurationManager.AppSettings.Get("firstBoot");
		if (v is not null)
		{
			bool i = false;
			try
			{
				i = Convert.ToBoolean(v);
			}
			catch (FormatException)
			{
				Log.Error("Boot: wrong isFirstRun format in App.config, " + i + " is not a boolean");
			}
			return i;
		}
		else
		{
			Log.Error("Boot: loadIsFirstRun() cannot load data from App.config");
			return false;
		}
	}

	private void verifyAppDataFolder()
	{
		string appDataPath = Program.getAppDataPath();
		string appDataFolder = Path.Combine(appDataPath, Program.appDataFolderName);
		if (!Directory.Exists(appDataFolder))
		{
			Directory.CreateDirectory(appDataFolder);
		}
	}
}
