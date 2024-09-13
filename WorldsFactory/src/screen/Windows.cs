using System;
using Avalonia.Controls;
using WorldsFactory.project.cards;
using WorldsFactory.screen.panelCards;

namespace WorldsFactory.screen;

/// <summary>
/// Contains methods for managing main windows
/// Knows about all opened <see cref="Card"/> and <see cref="MainWindow"/>
/// </summary>
public class Windows
{
	public static Dictionary<UserControl, Card> ViewCards = new Dictionary<UserControl, Card>();
	public static Dictionary<int, MainWindow> MainWindows = new Dictionary<int, MainWindow>();

	/// <summary>
	/// Opens new main window with OpenProjectCardView
	/// </summary>
	/// <param name="bootWindow"></param>
	/// <param name="program"></param>
	public static void openFirstMainWindow(Window bootWindow, Program program)
	{
		MainWindow newMainWindow = new MainWindow(
			new OpenProjectCardView(program.ProjectManager.AllProjects, program.ProjectManager.ProjectActions),
			"Open Project", program.ProjectManager.ProjectActions);
		MainWindows.Add(newMainWindow.Id, newMainWindow);
		newMainWindow.Show();
		bootWindow.Activate();
	}

	/// <summary>
	/// Close main window with given id, when last main window is closed, program is terminated
	/// </summary>
	/// <param name="id"></param>
	public static void closeMainWindow(int id)
	{
		MainWindows.Remove(id);
		if (MainWindows.Count == 0)
			Program.cancellationToken!.Cancel();
	}
}
