package net.demilich.metastone.game.spells;

import com.github.fromage.quasi.fibers.Suspendable;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardArrayList;
import net.demilich.metastone.game.cards.CardCatalogue;
import net.demilich.metastone.game.cards.CardList;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.spells.desc.filter.EntityFilter;
import net.demilich.metastone.game.spells.desc.source.CardSource;
import net.demilich.metastone.game.targeting.EntityReference;

import java.util.Map;

/**
 * @deprecated This class is currently only used for tri-class card discoveries.
 */
@Deprecated
public class DiscoverFilteredCardSpell extends Spell {
	public static SpellDesc create(EntityReference target, SpellDesc spell) {
		Map<SpellArg, Object> arguments = new SpellDesc(DiscoverFilteredCardSpell.class);
		arguments.put(SpellArg.TARGET, target);
		arguments.put(SpellArg.SPELL, spell);
		return new SpellDesc(arguments);
	}

	@Override
	@Suspendable
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		EntityFilter cardFilter = (EntityFilter) desc.get(SpellArg.CARD_FILTER);
		EntityFilter[] cardFilters = (EntityFilter[]) desc.get(SpellArg.CARD_FILTERS);
		CardList cards = CardCatalogue.query(context.getDeckFormat());
		CardSource cardSource = (CardSource) desc.get(SpellArg.CARD_SOURCE);
		if (cardSource != null) {
			cards = cardSource.getCards(context, source, player);
		}
		int count = desc.getValue(SpellArg.HOW_MANY, context, player, target, source, 3);
		CardList discoverCards = new CardArrayList();

		if (cardFilters != null) {
			for (EntityFilter filter : cardFilters) {
				CardList result = new CardArrayList();
				for (Card card : cards) {
					if (filter == null || filter.matches(context, player, card, source)) {
						result.addCard(card);
					}
				}

				if (!result.isEmpty()) {
					discoverCards.addCard(context.getLogic().getRandom(result));
				}
			}
		} else {
			CardList result = new CardArrayList();
			for (Card card : cards) {
				if (cardFilter == null || cardFilter.matches(context, player, card, source)) {
					result.addCard(card);
				}
			}
			discoverCards = new CardArrayList();

			for (int i = 0; i < count; i++) {
				if (!result.isEmpty()) {
					Card card = null;
					do {
						card = context.getLogic().getRandom(result);
						result.remove(card);
					} while (discoverCards.containsCard(card));
					if (card != null) {
						discoverCards.addCard(card);
					}
				}
			}
		}
		if (!discoverCards.isEmpty()) {
			SpellUtils.castChildSpell(context, player, SpellUtils.discoverCard(context, player, source, desc, discoverCards.getCopy()).getSpell(), source, target);
		}
	}

}
