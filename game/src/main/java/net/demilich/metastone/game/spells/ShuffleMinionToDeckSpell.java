package net.demilich.metastone.game.spells;

import com.github.fromage.quasi.fibers.Suspendable;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.spells.desc.SpellDesc;

/**
 * Peacefully removes the {@code target} minion and shuffles it into the caster's deck. Peaceful removal does not
 * trigger deathrattles. Enchantments are kept on targets with {@link net.demilich.metastone.game.cards.Attribute#KEEPS_ENCHANTMENTS}.
 * <p>
 * For <b>example</b>, this shuffles all minions into the opponent's deck:
 * <pre>
 *   {
 *     "class": "ShuffleMinionToDeckSpell",
 *     "target": "ALL_MINIONS",
 *     "targetPlayer": "OPPONENT"
 *   }
 * </pre>
 */
public class ShuffleMinionToDeckSpell extends ShuffleToDeckSpell {

	@Override
	@Suspendable
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		if (!target.isDestroyed()) {
			context.getLogic().removeActor((Minion) target, true);
		}

		super.onCast(context, player, desc, source, target);
	}

}
