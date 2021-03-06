package net.demilich.metastone.game.spells;

import com.github.fromage.quasi.fibers.Suspendable;
import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.minions.Minion;
import net.demilich.metastone.game.spells.desc.SpellArg;
import net.demilich.metastone.game.spells.desc.SpellDesc;
import net.demilich.metastone.game.targeting.EntityReference;

import java.util.Map;

/**
 * Takes control of a {@code target}, changing its owner to the caster.
 * <p>
 * Mind control destroys the minion if the new owner does not have enough space on the battlefield to take control of
 * it.
 * <p>
 * A mind controlled minion gains summoning sickness.
 *
 * @see net.demilich.metastone.game.spells.custom.MindControlOneTurnSpell for the version that lasts one turn and does
 * 		not add summoning sickness.
 * @see net.demilich.metastone.game.logic.GameLogic#mindControl(Player, Minion) for the full mind control rules.
 */
public class MindControlSpell extends Spell {

	public static SpellDesc create(EntityReference target, TargetPlayer targetPlayer, boolean randomTarget) {
		Map<SpellArg, Object> arguments = new SpellDesc(MindControlSpell.class);
		arguments.put(SpellArg.TARGET, target);
		arguments.put(SpellArg.RANDOM_TARGET, randomTarget);
		arguments.put(SpellArg.TARGET_PLAYER, targetPlayer);
		return new SpellDesc(arguments);
	}

	@Override
	@Suspendable
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity source, Entity target) {
		Minion minion = (Minion) target;
		context.getLogic().mindControl(player, minion);
	}

}
