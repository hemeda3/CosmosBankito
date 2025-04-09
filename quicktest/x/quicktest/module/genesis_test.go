package quicktest_test

import (
	"testing"

	keepertest "quicktest/testutil/keeper"
	"quicktest/testutil/nullify"
	quicktest "quicktest/x/quicktest/module"
	"quicktest/x/quicktest/types"

	"github.com/stretchr/testify/require"
)

func TestGenesis(t *testing.T) {
	genesisState := types.GenesisState{
		Params: types.DefaultParams(),

		// this line is used by starport scaffolding # genesis/test/state
	}

	k, ctx := keepertest.QuicktestKeeper(t)
	quicktest.InitGenesis(ctx, k, genesisState)
	got := quicktest.ExportGenesis(ctx, k)
	require.NotNil(t, got)

	nullify.Fill(&genesisState)
	nullify.Fill(got)

	// this line is used by starport scaffolding # genesis/test/assert
}
