using Avalonia;
using Avalonia.Controls;
using Avalonia.Interactivity;
using Avalonia.Markup.Xaml;
using Avalonia.Platform.Storage;
using Avalonia.Threading;
using Avalonia.VisualTree;
using NP.Utilities;
using WorldsFactory.project;
using WorldsFactory.world.ids;

namespace WorldsFactory.works.storyInterpreterWork.export.ui;

public partial class ExportStoryInterpretersDataView : UserControl
{
	ICurrentlyOpenedProject currentlyOpenedProject;
	TextBox pathTextBox;
	TextBlock exportStatusTextBlock;
	
	public ExportStoryInterpretersDataView()
	{
		throw new NotImplementedException();
	}
	
	public ExportStoryInterpretersDataView(ICurrentlyOpenedProject currentlyOpenedProject)
	{
		InitializeComponent();
		this.currentlyOpenedProject = currentlyOpenedProject;

		var exportButton = this.FindControl<Button>("ExportButton")!;
		exportButton.Click += ExportButton_Click!;

		var selectFolderButton = this.FindControl<Button>("SelectFolderButton")!;
		selectFolderButton.Click += SelectFolder_Click!;

		exportStatusTextBlock = this.FindControl<TextBlock>("ExportStatus")!;
		pathTextBox = this.FindControl<TextBox>("PathInput")!;
	}

	public async void ExportButton_Click(object sender, RoutedEventArgs e)
	{
		string? path = pathTextBox.Text;
		if (path is null || path.IsNullOrEmpty())
		{
			pathTextBox.Focus();
			return;
		}

		if (this.GetVisualRoot() is MainWindow mainWindow)
		{
			await mainWindow.StorageProvider.TryGetFolderFromPathAsync(path)
				.ContinueWith(task =>
				{
					Dispatcher.UIThread.InvokeAsync(async () =>
					{
						if (task.Result is null)
						{
							pathTextBox.Focus();
							mainWindow.Notifications.ShowErrorNotification("invalid_path_title", "invalid_path");
							return;
						}

						if (currentlyOpenedProject.Works is not null)
						{

							await currentlyOpenedProject.Works.Export(task.Result, currentlyOpenedProject.World)
								.ContinueWith(task =>
								{
									Dispatcher.UIThread.InvokeAsync(() =>
									{
										if (task.IsFaulted)
										{
											var exception = task.Exception?.InnerException;
											handleExportException(exception!, mainWindow);
											return;
										}
										else
										{
											mainWindow.Notifications.ShowSuccessNotification("export_success_title", "export_success");
											exportStatusTextBlock.Text = "Export successful";
											exportStatusTextBlock.Foreground = Avalonia.Media.Brushes.Green;
										}
										exportStatusTextBlock.FontWeight = Avalonia.Media.FontWeight.SemiBold;
										exportStatusTextBlock.FontStyle = Avalonia.Media.FontStyle.Italic;
									});

								});

						}
						else throw new NullReferenceException("Works is null");

					});
				});
		}
	}

	private void handleExportException(Exception exception, MainWindow mainWindow)
	{

		if (exception is not null && exception is ExportWholeWorld.SyntaxErrorException e)
		{
			mainWindow.Notifications.ShowErrorNotification("syntax_error_title", "syntax_error_title");
			exportStatusTextBlock.Text = $"Message: {e.Message}\n"
				+ $"In class {e.Class}, in method {e.Method}\n"
				+ $"Error in line {e.Line}, in column {e.Column}\n";
			exportStatusTextBlock.Foreground = Avalonia.Media.Brushes.Red;

		}
		else if (exception is not null && exception is ExportWholeWorld.UnknownEventReferenceException ex)
		{
			mainWindow.Notifications.ShowErrorNotification("unknown_event_reference_title", "unknown_event_reference");
			exportStatusTextBlock.Text = $"Message: {ex.Message}\n"
				+ $"In evemt {ex.ProblematicEvent}, unknown event reference {ex.UnknownEventReference}\n";
			exportStatusTextBlock.Foreground = Avalonia.Media.Brushes.Red;
		}
		else if (exception is ExportEvents.NoEntryEventException)
		{
			mainWindow.Notifications.ShowErrorNotification("no_entry_event_title", "no_entry_event");
			exportStatusTextBlock.Text = $"Message: {exception.Message}\n";
			exportStatusTextBlock.Foreground = Avalonia.Media.Brushes.Red;
		}
		else if(exception is ConceptNotFoundException){
			mainWindow.Notifications.ShowErrorNotification("concept_not_found_title", "concept_not_found");
			exportStatusTextBlock.Text = $"Message: {exception.Message}\n";
			exportStatusTextBlock.Foreground = Avalonia.Media.Brushes.Red;
		}
		else if (exception is TooManyPropertiesWithSameNameException)
		{
			mainWindow.Notifications.ShowErrorNotification("too_many_properties_with_same_name_title", "too_many_properties_with_same_name");
			exportStatusTextBlock.Text = $"Message: {exception.Message}\n";
			exportStatusTextBlock.Foreground = Avalonia.Media.Brushes.Red;
		}
		else
		{
			if (exception is not null)
			{
				mainWindow.Notifications.ShowErrorNotification("export_error_title", "export_error");
				exportStatusTextBlock.Text = $"Error: {exception.Message}";
				exportStatusTextBlock.Foreground = Avalonia.Media.Brushes.Red;
			}
		}
	}

	public async void SelectFolder_Click(object sender, RoutedEventArgs e)
	{
		Uri? path = await selectFolder(this);

		if (path is not null)
		{
			pathTextBox.Text = path.AbsolutePath!;
		}
	}

	private async Task<Uri?> selectFolder(UserControl userControl)
	{
		// Get top level from the current control. Alternatively, you can use Window reference instead.
		var topLevel = TopLevel.GetTopLevel(userControl)!;

		// Start async operation to open the dialog.
		var folders = await topLevel.StorageProvider.OpenFolderPickerAsync(new FolderPickerOpenOptions
		{
			Title = "Select folder",
			AllowMultiple = false
		});


		if (folders.Count >= 1)
		{
			var folder = folders[0];
			return folder.Path;
		}
		return null;
	}

}