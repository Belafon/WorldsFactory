using System;
using System.Linq;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Controls.Shapes;
using Avalonia.Input;
using Avalonia.Interactivity;
using Avalonia.VisualTree;

namespace WorldsFactory.screen.panelCards;


/// <summary>
/// Custom Avalonia behaviour for dragging a card.
/// Monitors the pointer movement and releases.
/// Handles showing regions where the card can be dropped.
/// <see cref="DragCardPressedBehaviour"/> is responsible for dragging 
/// and dropping the tab of a card.
/// </summary>
public class DragCardMoveBehavior
{
	#region IsSet Attached Avalonia Property
	public static bool GetIsSet(Control obj)
	{
		return obj.GetValue(IsSetProperty);
	}

	public static void SetIsSet(Control obj, bool value)
	{
		obj.SetValue(IsSetProperty, value);
	}

	public static readonly AttachedProperty<bool> IsSetProperty =
		AvaloniaProperty.RegisterAttached<DragCardMoveBehavior, Control, bool>(
			"IsSet"
		);
	#endregion IsSet Attached Avalonia Property

	static DragCardMoveBehavior()
	{
		IsSetProperty.Changed.Subscribe(OnIsSetChanged);
	}

	private static void OnIsSetChanged(AvaloniaPropertyChangedEventArgs<bool> args)
	{
		Control control = (Control)args.Sender;

		control.AddHandler(DragDrop.DragOverEvent, Control_PointerMoved);
		control.AddHandler(DragDrop.DropEvent, Control_PointerReleased);
		control.AddHandler(DragDrop.DragLeaveEvent, Control_PointerLeave);
	}

	private static void Control_PointerMoved(object? sender, DragEventArgs e)
	{
		Control cardBody = (Control)sender!;
		PanelCardView panel = cardBody.GetVisualAncestors().OfType<PanelCardView>().FirstOrDefault()!;

		panel.UpadteOnPointerMovedOverCardWhileCardDragged(e, cardBody);
	}
	private static void Control_PointerLeave(object? sender, RoutedEventArgs e)
	{
		if(e.Source is Rectangle rectangle
			&& rectangle.Name == "cardSplittingRectangle")
		{
			Control cardBody = (Control)sender!;
			PanelCardView panel = cardBody.GetVisualAncestors().OfType<PanelCardView>().FirstOrDefault()!;
			panel.UpdateOnPointerLeaveWhileCardDragged();
		}
	}
	private static void Control_PointerReleased(object? sender, DragEventArgs e)
	{
		Control cardBody = (Control)sender!;
		PanelCardView panel = cardBody.GetVisualAncestors().OfType<PanelCardView>().FirstOrDefault()!;
		panel.UpdateOnPointerReleasedOverCardWhileCardDragged(e, cardBody);
	}
}
