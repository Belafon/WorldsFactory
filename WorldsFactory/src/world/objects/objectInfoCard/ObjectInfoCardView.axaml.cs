using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;

namespace WorldsFactory.world.objects.objectInfoCard;

public partial class ObjectInfoCardView : UserControl
{
    public WFObject Object { get; private set; }
    public ObjectInfoCardView(WFObject wfObject)
    {
        InitializeComponent();
        Object = wfObject;
        var containerOfInitMethod = this.FindControl<DockPanel>("contentContainer")!; 
        var initMethodEditorView = new InitMethodUpdateView(wfObject);
        containerOfInitMethod.Children.Add(initMethodEditorView);
    }

    private void InitializeComponent()
    {
        AvaloniaXamlLoader.Load(this);
    }
}