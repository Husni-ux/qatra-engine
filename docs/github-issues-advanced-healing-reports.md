# GitHub Issues — Advanced Healing Reports

Suggested issues for continuing Phase 3.18.6:

## 1. Add side-by-side DOM snapshot for healed elements
Capture a small sanitized HTML snippet for the failed primary context and the selected fallback element.

## 2. Add screenshot crops around healed elements
Capture element-level screenshots or full screenshot with highlighted bounding boxes.

## 3. Add CI artifact summary for healing reports
Generate a compact Markdown summary that can be posted in GitHub Actions.

## 4. Add report severity scoring
Combine confidence, risk, ambiguity, and semantic mismatch into a single report severity.

## 5. Add review decision file
Allow teams to mark locator patches as accepted/rejected in a review JSON file.

## 6. Add historical healing trends
Track how often a locator was healed, when it started failing, and how often the same fallback was selected.

## 7. Add Page Object mapping metadata
Allow users to declare class name and field name so the patch suggestion can point to the exact Page Object field.
