attempt at creating proper abstraction for re-usable view components across different contexts

# Item
* ~~Override `equals` when in RecyclerView and diff is used to detect if contents are the same~~

## ViewGroup
* _if wrapping happens_, then diff must use a different logic, to check viewType properly and then id,
  currently class is compared directly
  
## Wrapping
* distinct viewType for a wrapping chain
  `Padding Margin Item` and `Margin Padding Item` are not the same 
  `Padding Margin Item` and `Padding Item` are not the same
* uniqueness of generated viewType combined with reliable generator that can 
  be proven and does not rely on some randomness of hashcode
  
# Benefits
* adding background would require modification of existing components, so delegation
* for single file - refactoring, for example cleaning
 