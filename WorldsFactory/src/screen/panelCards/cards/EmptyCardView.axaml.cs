using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;

namespace WorldsFactory.screen.panelCards.cards
{
    public partial class EmptyCardView : UserControl
    {
        public EmptyCardView()
        {
            InitializeComponent();
        }

        private void InitializeComponent()
        {
            AvaloniaXamlLoader.Load(this);
        }
    }
}