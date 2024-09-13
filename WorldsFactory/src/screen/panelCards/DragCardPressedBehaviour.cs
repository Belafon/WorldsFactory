using System;
using System.Linq;
using Avalonia;
using Avalonia.Controls;
using Avalonia.Input;
using Avalonia.Media;
using Avalonia.VisualTree;
using Serilog;

namespace WorldsFactory.screen.panelCards;

/// <summary>
/// Custom Avalonia behaviour for dragging a card.
/// Monitors the pointer movement and releases.
/// Hanles draging a tab of a card and dropping a tab of a card.
/// <see cref="DragCardMoveBehavior"/> is responsible for showing regions
/// where the card can be dropped.
/// </summary>
public class DragCardPressedBehaviour
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
		AvaloniaProperty.RegisterAttached<DragCardPressedBehaviour, Control, bool>(
			"IsSet"
		);

	#endregion IsSet Attached Avalonia Property


	#region Card Attached Avalonia Property
	public static Card? GetCard(Control obj)
	{
		return obj.GetValue(CardProperty);
	}

	public static void SetCard(Control obj, Card value)
	{
		obj.SetValue(CardProperty, value);
	}

	public static readonly AttachedProperty<Card?> CardProperty =
		AvaloniaProperty.RegisterAttached<CallActionOnEventBehavior, Control, Card?>
	(
			"Card"
		);
	#endregion Card Attached Avalonia Property

	static DragCardPressedBehaviour()
	{
		IsSetProperty.Changed.Subscribe(OnIsSetChanged);
	}

	private static void OnIsSetChanged(AvaloniaPropertyChangedEventArgs<bool> args)
	{
		Control control = (Control)args.Sender;

		if (args.NewValue.Value == true)
			control.PointerPressed += Control_PointerPressed;
		else
			control.PointerPressed -= Control_PointerPressed;

		control.AddHandler(DragDrop.DragOverEvent, Control_PointerMoved);
		control.AddHandler(DragDrop.DropEvent, Control_PointerReleased);
	}
	private static void Control_PointerPressed(object? sender, PointerEventArgs e)
	{
		if (sender is not null)
		{
			Card? card = GetCard((Control)sender);
			if (card is not null)
			{
				card.DragCard(e);
			}
		}
	}

	private static void Control_PointerMoved(object? sender, DragEventArgs e)
	{
		if (sender is not null)
		{
			Control control = (Control)sender!;

			if (control is StackPanel tab)
			{
				PanelCardView panel = tab.GetVisualAncestors().OfType<PanelCardView>().FirstOrDefault()!;
				Card? card = GetCard((Control)sender);

				if (card is not null)
					panel.OnPointerMovedOverTabWhileCardDragged(e, panel, card, tab);
			}
		}
	}
	private static void Control_PointerReleased(object? sender, DragEventArgs e)
	{
		if (sender is not null)
		{
			Control clickedControl = (Control)sender!;
			PanelCardView panel = clickedControl.GetVisualAncestors().OfType<PanelCardView>().FirstOrDefault()!;
			//StackPanel tab = clickedControl.GetVisualAncestors().OfType<StackPanel>().FirstOrDefault()!; 
			if (clickedControl is StackPanel tab)
			{
				Card? card = GetCard((Control)sender);

				if (card is not null)
					panel.UpdateOnPointerReleasedOverTabWhileCardDragged(e, panel, card, tab);
			}
		}
	}
}
