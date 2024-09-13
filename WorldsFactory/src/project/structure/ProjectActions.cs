using System.Net;
using System.Diagnostics;
using System.ComponentModel.DataAnnotations;
using System.IO;
using WorldsFactory.screen.panelCards;

using System;
using Avalonia.Controls;
using WorldsFactory.screen;
using Serilog;
using Avalonia.Controls.Notifications;
using WorldsFactory.screen.panelCards.cards;
using WorldsFactory.world;
using Avalonia.Threading;
using WorldsFactory.project.cards;

namespace WorldsFactory.project;
/// <summary>
/// Contains projects interface for the frontend. 
/// Represents layer between frontend and backend.
/// </summary>
public class ProjectActions
{
	private AllProjects allProjects;

	private ProjectCreator projectCreateor = new ProjectCreator();
	public ProjectActions(AllProjects allProjects)
	{
		this.allProjects = allProjects;
	}
	/// <summary>
	/// Opens new workspace with project
	/// </summary>
	/// <param name="projectReference"></param>
	public void OpenProject(ProjectReference projectReference, TopLevel topLevel)
	{
		if (!allProjects.ValidateProjectStructure(projectReference.Path, out string error))
		{
			if (topLevel is MainWindow window)
				window.Notifications.ShowNotification("invalid_project_structure_title", "invalid_project_structure", NotificationType.Error);
			allProjects.RemoveProject(projectReference);
			return;
		}

		Log.Information("Opening project {projectName}", projectReference.Name);
		if (topLevel is MainWindow mainWindow)
		{
			// loads all worlds data and checks the structure
			mainWindow.FocusCardHistory.Clear();

			try
			{
				var project = new CurrentlyOpenedProject(projectReference, mainWindow.StorageProvider);
				var overviewProjectActions = new OverviewProjectActions(project, mainWindow);
				var overviewProjectView = new OverviewProjectView(project.World, overviewProjectActions);

				var overviewCard = new Card("Project overview", overviewProjectView, null);
				mainWindow.ClearAllCards(overviewCard);

				var card = mainWindow.CardsContainers[overviewProjectView];
				overviewProjectActions.SetCardContainer(card);

				allProjects.OpenProject(projectReference);

				mainWindow.BindWithProject(project.World, project);
			}
			catch (CannotCreateFolderException)
			{
				if (topLevel is MainWindow window)
					window.Notifications.ShowNotification("cannot_create_folder_title", "cannot_create_folder", NotificationType.Error);
				return;
			}
			catch (InvalidWorldStructureException)
			{
				if (topLevel is MainWindow window)
					window.Notifications.ShowNotification("invalid_world_structure_title", "invalid_world_structure", NotificationType.Error);
				return;
			}
		}
		else throw new NotImplementedException();
	}

	/// <summary>
	/// Opens new card view with project creation form in the window.
	/// </summary>
	public Card? ShowCreateNewProjectCardView(MainWindow window)
	{
		// TODO: Implement this method
		return null;
	}

	/// <summary>
	/// Opens new card view with project creation form in concrete cardView.
	/// </summary>
	/// <param name="cardView"></param>
	public void ShowCreateNewProjectCardView(Card? card)
	{
		if (card is not null)
		{
			card.container?.AppendCard(new NewProjectCardView(allProjects, this), "New project");
		}
		else
		{
			throw new NullReferenceException();
		}
	}

	/// <summary>
	/// Creates new project based on the paraemters and opens it, 
	/// if the creation was successful.
	/// </summary>
	/// <param name="parameters"></param>
	/// <param name="topLevel"></param>
	/// <returns></returns>
	public async void CreateProject(ProjectCreator.Parameters parameters, TopLevel topLevel)
	{
		await projectCreateor.CreateProject(parameters, topLevel, projectsRootPath =>
		{
			// open project
			var projectReference = allProjects.AddNewProjectFromDirectory(projectsRootPath);
			OpenProject(projectReference, topLevel);
		});
	}

	/// <summary>
	/// Opens new card view with project creation form in the window.
	/// </summary>
	/// <param name="card"></param>
	public void ShowOpenProjectCardView(Card card)
	{
		card.container?.AppendCard(new OpenProjectCardView(allProjects, this), "Open project");
	}

	internal void ShowProjectOverviewCardView(Card card, MainWindow mainWindow)
	{
		if (card is not null)
		{
			var project = mainWindow.OpenedProject!;
			var overviewProjectActions = new OverviewProjectActions(project, mainWindow);
			var overviewProjectView = new OverviewProjectView(project.World, overviewProjectActions);
			card.container?.AppendCard(overviewProjectView, "Project overview");
			if(card.container is not null)
				overviewProjectActions.SetCardContainer(card.container);
		}
	}
}