using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using WorldsFactory.world.library.classStructure;
using System.Reactive.Linq;

namespace WorldsFactory.world.events.eventsInfoCard;

public partial class EventBasicInfoView : UserControl
{
	public EventBasicInfoView()
	{
		throw new NotImplementedException();
	}
	public EventBasicInfoView(IEvent event_)
	{
		InitializeComponent();

		var containerOfConditionMethod = this.FindControl<DockPanel>("TextEditorWithPythonForConditionMehod")!;
		var containerOfActionMethod = this.FindControl<DockPanel>("TextEditorWithPythonForActionMethod")!;
		var conditionMethodEditor = new TextEditorWithPythonView(event_.Condition, 47)
		{
			OnTextUpdated = (s) =>
			{
				event_.Condition.Body!.Code = s;
			}
		};
		var actionMethodEditor = new TextEditorWithPythonView(event_.Action, 47)
		{
			OnTextUpdated = (s) =>
			{
				event_.Action.Body!.Code = s;
			}
		};
		containerOfConditionMethod.Children.Add(conditionMethodEditor);
		containerOfActionMethod.Children.Add(actionMethodEditor);
		DataContext = new EventBasicInfoViewModel(event_, conditionMethodEditor, actionMethodEditor);
	}
}

internal class EventBasicInfoViewModel
{
	private IEvent event_;
	private TextEditorWithPythonView conditionMethodEditor;
	private TextEditorWithPythonView actionMethodEditor;

	public EventBasicInfoViewModel(IEvent event_, TextEditorWithPythonView conditionMethodEditor, TextEditorWithPythonView actionMethodEditor)
	{
		this.event_ = event_;
		this.conditionMethodEditor = conditionMethodEditor;
		this.actionMethodEditor = actionMethodEditor;
	}
}