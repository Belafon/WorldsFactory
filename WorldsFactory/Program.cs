using Avalonia;
using Avalonia.Controls;
using Serilog;
using System;
using System.Configuration;
using System.Diagnostics;
using System.Threading;
using System.Runtime.InteropServices;
using System.IO;


using WorldsFactory.screen;
using WorldsFactory.boot;
using WorldsFactory.keyListener;

using WorldsFactory.project;

namespace WorldsFactory;

/// <summary>
/// Contains the main method
/// Sets up Avalonia UI
/// Calls boot process
/// </summary>
public class Program
{
	internal static readonly string appDataFolderName = "WorldsFactory";
	public readonly ProjectManager ProjectManager = new ProjectManager();
	public string version { get; private set; }
	private bool isFirstRun;
	public KeyListener? keyListener;
	
	/// <summary>
	/// The state is saved in the App settings configuration file
	/// </summary>
	/// <value>Tells if the program is run for the first time</value>
	public bool IsFirstRun
	{
		get { return isFirstRun; }
		set
		{
			ConfigurationManager.AppSettings.Set("firstBoot", value.ToString());
		}
	}
	public Program(string version, bool isFirstRun)
	{
		this.version = version;
		this.isFirstRun = isFirstRun;
	}

	/// <summary>
	/// Initialization code. Don't use any Avalonia, third-party APIs or any
	/// SynchronizationContext-reliant code before AppMain is called: things aren't initialized
	/// yet and stuff might break.
	/// </summary>
	/// <param name="args"></param>
	[STAThread]
	public static void Main(string[] args)
	{
		BuildAvaloniaApp().Start(AppMain, args);
	}

	// Avalonia configuration, don't remove; also used by visual designer.
	public static AppBuilder BuildAvaloniaApp()
		=> AppBuilder.Configure<App>()
			.UsePlatformDetect();


	/// <summary>
	/// Cancellation token source is used to stop the main loop of the UI thread
	/// and to terminate the program
	/// </summary>
	public static CancellationTokenSource? cancellationToken;

	/// <summary>
	/// Application entry point. Avalonia is completely initialized.
	/// </summary>
	/// <param name="app">The application instance</param>
	/// <param name="args">The command line arguments</param>
	static void AppMain(Application app, string[] args)
	{
		Log.Logger = new LoggerConfiguration()
				.WriteTo.Console()
				.CreateLogger();

		Log.Information("Lets start the program.");

		StartLoadingWindow window = new StartLoadingWindow();

		Thread bootThread = new Thread(() => startBoot(window));
		bootThread.Start();

		window.Show();

		// A cancellation token source that will be used to stop the main loop
		cancellationToken = new CancellationTokenSource();

		// Start the main loop
		app.Run(cancellationToken.Token);
	}

	/// <summary>
	/// Starts the boot process of the program
	/// </summary>
	/// <param name="bootWindow"></param>
	private static void startBoot(Window bootWindow)
	{
		new Boot(bootWindow);
	}

	/// <summary>
	/// Returns the path to the application data directory according to the current operating system
	/// </summary>
	/// <returns>The path to the application data directory</returns>
	public static string getAppDataPath()
	{
		string directoryPath;

		// Check the operating system and set the directory path accordingly
		if (RuntimeInformation.IsOSPlatform(OSPlatform.Windows))
		{
			directoryPath = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
		}
		else if (RuntimeInformation.IsOSPlatform(OSPlatform.Linux))
		{
			directoryPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.UserProfile), ".config");
		}
		else if (RuntimeInformation.IsOSPlatform(OSPlatform.OSX))
		{
			directoryPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.Personal), "Library", "Application Support");
		}
		else
		{
			throw new NotSupportedException("Unsupported operating system.");
		}

		return directoryPath;
	}
}

