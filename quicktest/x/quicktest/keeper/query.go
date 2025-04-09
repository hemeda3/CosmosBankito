package keeper

import (
	"quicktest/x/quicktest/types"
)

var _ types.QueryServer = Keeper{}
