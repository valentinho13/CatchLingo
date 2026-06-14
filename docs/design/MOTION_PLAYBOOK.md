# CatchLingo Motion Playbook v1

This playbook complements `MOTION_GUIDELINES.md`. If that file is absent in this repository, this
document acts as the current motion reference until the detailed guidelines are added.

## Intent

CatchLingo motion should feel like a warm field journal coming alive: tactile, calm, premium, and
emotionally clear. Motion is part of the product experience, not decorative noise.

## Principles

- Motion should clarify what changed, especially when a word becomes a collected find.
- The catch moment follows the product rhythm: notice, pull, hold.
- Magnet and suction are the only catch metaphor.
- Warm amber light, soft motes, small scale changes, and gentle easing are preferred.
- Camera overlays must never become scanner UI.
- Avoid reticles, bounding boxes, grid lines, target locks, confidence values, or debug labels.
- Keep animations short enough to preserve the live-camera feeling.
- Haptics should be subtle and tied to confirmed events, not every detection.

## Magnet Catch

The first production catch motion should remain restrained:

- Trigger only after a word is actually persisted.
- Use a small number of amber motes moving toward the card destination.
- Make convergence directional enough to read as suction.
- End in a held specimen-style card.
- Do not show continuous object labels or intermediate ML results.

## Timing

- Mote convergence: about 600-750 ms.
- Catch card visibility: about 2-3 seconds.
- Crossfades and scale changes should stay under 350 ms unless a product moment needs more weight.

## Performance

- Prefer one Canvas for particle-like effects.
- Cap mote counts.
- Avoid adding dependencies for motion.
- Do not alter camera frame analysis cadence for visual polish.
