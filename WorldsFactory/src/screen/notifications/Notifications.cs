using System.Collections.Generic;
using System;
using Avalonia.Controls.Notifications;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Threading;

namespace WorldsFactory.screen
{
	/// <summary>
	/// System for showing notifications in the context of the given control.
	/// Note: <see href="https://github.com/AvaloniaUI/Avalonia/issues/5442"/>
	/// </summary>
	public class Notifications
	{
		/// <summary>
		/// Temporary list of all notification messages, // TODO
		/// that can be shown in concrete control.
		/// </summary>
		/// <typeparam name="string"></typeparam>
		/// <typeparam name="string"></typeparam>
		/// <returns></returns>
		private Dictionary<string, string> notificationMessages = new Dictionary<string, string>()
		{
			{"title", "title"},
			{"message", "message"},
			{"empty", ""},
			{"invalid_project_structure_title", "Invalid Project Structure"},
			{"invalid_project_structure", "The selected project has an invalid structure."},
			{"folder_with_same_name_title", "Folder with Same Name"},
			{"folder_with_same_name", "A folder with the same name already exists."},
			{"error_in_folder_creation_title", "Error in Folder Creation"},
			{"error_in_folder_creation", "There was an error creating the folder."},
			{"error_in_project_file_creation_title", "Error in Project File Creation"},
			{"error_in_project_file_creation", "There was an error creating the project file."},
			{"empty_name_or_path_title", "Empty Name or Path"},
			{"empty_name_or_path", "The name of the project must contain at least one character."},
			{"invalid_world_structure_title", "Invalid World Structure"},
			{"invalid_world_structure", "The base worlds structure is invalid."},
			{"error_in_world_folder_creation_title", "Error in World Folder Creation"},
			{"error_in_world_folder_creation", "There was an error creating the world folder."},
			{"error_in_world_file_creation_title", "Error in World File Creation"},
			{"error_in_world_file_creation", "There was an error creating the world file."},
			{"selected_world_empty_path_title", "Selected World Empty Path"},
			{"selected_world_empty_path", "The selected world path cannot be empty."},
			{"empty_new_world_name_title", "Empty New World Name"},
			{"empty_new_world_name", "The name of the new world must contain at least one character."},
			{"no_world_selected_title", "No World Specified"},
			{"no_world_selected", "The name for a new world or an existing world must be specified."},
			
			{"error_existing_world_file_not_found_title", "Existing World File Not Found"},
			{"error_existing_world_file_not_found", "The existing world file was not found on the given path."},
			{"error_existing_world_file_has_invalid_structure_title", "Existing World Wtih Invalid Structure"},
			{"error_existing_world_file_has_invalid_structure", "The existing world file has an invalid structure."},
			{"cannot_create_folder_title", "Folder Creation Failed"},
			{"cannot_create_folder", "The system encountered an error and could not create the specified folder. Please try again later."},
			{"cannot_create_new_event_title", "Event with this name already exists."},
			{"cannot_create_new_object_title", "Object with this name already exists."},
			{"cannot_create_new_class_title", "Class with this name already exists."},
			{"cannot_create_new_linear_event_title", "Cannot create new linear event"},
			{"event_container_with_this_name_already_exists_title", "Event Container already exists."},
			{"event_container_with_this_name_already_exists", "An event container with this name already exists."},

			//export
			{"invalid_path_title", "Invalid Path"},
			{"invalid_path", "The selected path is invalid."},
			{"export_success_title", "Export Success"},
			{"export_success", "The export was successful."},
			{"syntax_error_title", "Syntax Error"},
			{"syntax_error", "There was a syntax error in the code."},
			{"no_entry_event_title", "No Entry Event"},
			{"no_entry_event", "The event tree has no entry event. At least one is required."},
			{"too_many_properties_with_same_name_title", "Too Many Properties With Same Name"},
			{"too_many_properties_with_same_name", "There are too many properties with the same name, check the property declarations also in ancestor classes."},
			{"concept_not_found_title", "Concept Not Found"},
			{"concept_not_found", "The concept was not found in the world."},
			{"export_error_title", "Export Error"},
			{"export_error", "There was an error during the export."}
		};


		private Control control;


		/// <summary>
		/// System for showing notifications in context of the given control.
		/// </summary>
		public Notifications(Control control)
		{
			this.control = control;
		}

		public void ShowNotification(string title, string message, NotificationType type)
		{
			var not = new Notification(notificationMessages[title], notificationMessages[message], type);

			var nm = new WindowNotificationManager(TopLevel.GetTopLevel(control)!)
			{
				Position = NotificationPosition.TopRight,
				MaxItems = 1
			};
			nm.TemplateApplied += (sender, args) =>
			{
				nm.Show(not);
			};
		}

		public void ShowInformationNotification(string title, string message)
		{
			var not = new Notification(notificationMessages[title], notificationMessages[message], NotificationType.Information);

			var nm = new WindowNotificationManager(TopLevel.GetTopLevel(control)!)
			{
				Position = NotificationPosition.TopRight,
				MaxItems = 1
			};
			nm.TemplateApplied += (sender, args) =>
			{
				nm.Show(not);
			};
		}

		public void ShowSuccessNotification(string title, string message)
		{
			var not = new Notification(notificationMessages[title], notificationMessages[message], NotificationType.Success);

			var nm = new WindowNotificationManager(TopLevel.GetTopLevel(control)!)
			{
				Position = NotificationPosition.TopRight,
				MaxItems = 1
			};
			nm.TemplateApplied += (sender, args) =>
			{
				nm.Show(not);
			};
		}

		public void ShowWarningNotification(string title, string message)
		{
			var not = new Notification(notificationMessages[title], notificationMessages[message], NotificationType.Warning);

			var nm = new WindowNotificationManager(TopLevel.GetTopLevel(control)!)
			{
				Position = NotificationPosition.TopRight,
				MaxItems = 1
			};
			nm.TemplateApplied += (sender, args) =>
			{
				nm.Show(not);
			};
		}

		public void ShowErrorNotification(string title, string message)
		{
			var not = new Notification(notificationMessages[title], notificationMessages[message], NotificationType.Error);

			var nm = new WindowNotificationManager(TopLevel.GetTopLevel(control)!)
			{
				Position = NotificationPosition.TopRight,
				MaxItems = 1
			};
			nm.TemplateApplied += (sender, args) =>
			{
				nm.Show(not);
			};
		}

		public void ShowExample()
		{
			var not = new Notification("Test", "this is a test notification message", NotificationType.Success);
			var nm = new WindowNotificationManager(TopLevel.GetTopLevel(control)!)
			{
				Position = NotificationPosition.TopRight,
				MaxItems = 1
			};
			nm.TemplateApplied += (sender, args) =>
			{
				nm.Show(not);
			};
		}

	}
}