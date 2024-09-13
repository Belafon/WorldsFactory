using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using WorldsFactory.world.library.classStructure;

namespace WorldsFactory.world.objects.objectInfoCard;

public partial class InitMethodUpdateView : UserControl
{
	public InitMethodUpdateView(WFObject wfObject)
	{
		InitializeComponent();
		var containerOfInitMethod = this.FindControl<DockPanel>("TextEditorWithPythonForObjectsInitMehod")!;
		var initMethodEditor = new TextEditorWithPythonView(wfObject.InitMethod, 47)
		{
			OnTextUpdated = (s) =>
			{
				wfObject.InitMethod.Body!.Code = s;
			}
		};
		containerOfInitMethod.Children.Add(initMethodEditor);
	}
}