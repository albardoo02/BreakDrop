# BreakDrop
BreakDrop is a simple plugin for paper 1.21.11 that allows you to execute some functions when destroying blocks.

## Configuration syntax

Actual configuration file look like this:

```yaml
drops:
- functions:
  - skills: [breakskill2]
    chance: 0.008
  - skills: [sagyouherumetto]
    chance: 1
  condition:
    op: OR
    conditions:
    - inventory:
      - mythic_type: sagyouherumetto
        slot: helmet
    - inventory:
      - type: leather_helmet
        display_name: '&6&l作業&e&lヘルメット'
        slot: helmet
```

Configuration structure would look like this:

```typescript
type MythicItem = {
  mythic_type: string
}

type CrackShotItem = {
  crackshot_type: string
}

type MinecraftItem = {
  type: string // namespaced id or bukkit material
  custom_model_data?: number // int
  display_name?: string
  lore?: Array<string>
  enchantments?: { [id: string]: number }
  // tag?: string
}

type Item = MythicItem | CrackShotItem | MinecraftItem

type ItemStack = Item & {
  amount?: number // int (default: 0)
}

type Sound = {
  id: string
  volume: number // float
  pitch: number // float
}

type VariableCondition = {
  scope: 'caster' | 'global'
  key: string
  type: 'string' | 'int' | 'float'
  value: string | number // string | int | float
  op?: '=' | '!=' | '<' | '>' | '<=' | '>=' // defaults to '='
}

type Condition = {
  inventory?: Item & {
    slot?: 'helmet' | 'chestplate' | 'leggings' | 'boots' | 'mainhand' | 'offhand' // works on any slots if omitted
  }
  worlds?: Array<string>
  worlds_except?: Array<string>
  max_y_inclusive?: number // int
  min_y_inclusive?: number // int
  variables?: Array<VariableCondition>
}

type ConditionList = {
  op: 'OR' | 'AND'
  conditions: Array<Condition | ConditionList>
}

type SetVariable = {
  scope: 'caster' | 'global'
  key: string
  type: 'string' | 'int' | 'float'
  value: string | number // string | int | float
  op?: 'SET' | 'ADD' | 'SUBTRACT' // defaults to SET
}

type DropFunction = {
  skills?: Array<string>
  skills_random?: boolean // = false
  // items?: Array<ItemStack>
  // variables?: Array<SetVariable>
  // sounds?: Array<Sound>
  // messages?: Array<string>
  chance: number // double
  count?: number // int (default: 1)
  condition?: Condition | ConditionList
}

type Drop = {
  functions: Array<DropFunction>
  condition?: Condition | ConditionList
}

type Root = {
  drops: Array<Drop>
}
```
