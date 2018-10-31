package pbt.exercise3;

import java.util.*;
import java.util.stream.*;

public class BasketballGame {
	private final List<String> players;
	private final List<GameEvent> events = new ArrayList<>();

	public BasketballGame(List<String> players) {
		if (players.size() > 12) {
			throw new IllegalArgumentException("Not more than 12 players allowed");
		}
		this.players = players;
	}

	public List<String> players() {
		return Collections.unmodifiableList(players);
	}

	public void receiveEvent(GameEvent gameEvent) {
		if (!isPlayerRegistered(gameEvent.player())) {
			throw new IllegalArgumentException(String.format("Player %s is not registered", gameEvent.player()));
		}
		checkEventOrder(gameEvent);
		events.add(gameEvent);
	}

	private boolean isPlayerRegistered(String player) {
		return players.contains(player);
	}

	private void checkEventOrder(GameEvent gameEvent) {
		switch (gameEvent.type()) {
			case SUB_IN:
				if (isOnField(gameEvent.player())) {
					throw new EventOutOfOrder(String.format(
							"Player %s cannot be subed in at %s",
							gameEvent.player(),
							gameEvent.gameTime()
					));
				}
				break;
			case SUB_OUT:
				if (!isOnField(gameEvent.player())) {
					throw new EventOutOfOrder(String.format(
							"Player %s cannot be subed out at %s",
							gameEvent.player(),
							gameEvent.gameTime()
					));
				}
				break;
		}
	}

	private boolean isOnField(String player) {
		List<GameEvent> gameEventsReversed = eventsFor(player);
		Collections.reverse(gameEventsReversed);
		return gameEventsReversed
				.stream()
				.filter(GameEvent::isSubstitution)
				.map(GameEvent::type)
				.findFirst().orElse(GameEvent.Type.SUB_OUT) == GameEvent.Type.SUB_IN;
	}

	public List<GameEvent> events() {
		return Collections.unmodifiableList(events);
	}

	public List<GameEvent> eventsFor(String player) {
		return events().stream()
					   .filter(e -> e.player().equals(player))
					   .collect(Collectors.toList());
	}

	public PlayerStats statsFor(String player) {
		if (!isPlayerRegistered(player)) {
			throw new IllegalArgumentException(String.format("Player %s is not registered", player));
		}
		return new PlayerStats(eventsFor(player));
	}
}